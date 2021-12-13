package net.starlegacy.util.blockplacement;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static net.starlegacy.util.CoordinatesKt.*;

class BlockPlacementRaw {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final WeakHashMap<World, Long2ObjectOpenHashMap<BlockState[][][]>> worldQueues = new WeakHashMap<>();

	@NotNull
	private static BlockState[][][] emptyChunkMap() {
		// y x z array
		BlockState[][][] array = new BlockState[256][][];

		for (int y1 = 0; y1 < array.length; y1++) {
			BlockState[][] xArray = new BlockState[16][];

			for (int x1 = 0; x1 < xArray.length; x1++) {
				xArray[x1] = new BlockState[16];
			}

			array[y1] = xArray;
		}

		return array;
	}

	synchronized void queue(World world, Long2ObjectOpenHashMap<BlockState> queue) {
		Long2ObjectOpenHashMap<BlockState[][][]> worldQueue = worldQueues.computeIfAbsent(world, w -> new Long2ObjectOpenHashMap<>());

		addToWorldQueue(queue, worldQueue);
	}

	void addToWorldQueue(Long2ObjectOpenHashMap<BlockState> queue, Long2ObjectOpenHashMap<BlockState[][][]> worldQueue) {
		queue.forEach((coords, blockData) -> {
			int y = blockKeyY(coords);
			int x = blockKeyX(coords);
			int z = blockKeyZ(coords);

			int chunkX = x >> 4;
			int chunkZ = z >> 4;

			long chunkKey = chunkKey(chunkX, chunkZ);

			BlockState[][][] chunkQueue = worldQueue.computeIfAbsent(chunkKey, c -> emptyChunkMap());

			chunkQueue[y][x & 15][z & 15] = blockData;
		});
	}

	void flush(@Nullable Consumer<World> onComplete) {
		Preconditions.checkState(Bukkit.isPrimaryThread());

		if (worldQueues.isEmpty()) {
			return;
		}

		for (World world : new ArrayList<>(worldQueues.keySet())) {
			Long2ObjectOpenHashMap<BlockState[][][]> worldQueue = worldQueues.get(world);
			placeWorldQueue(world, worldQueue, onComplete, false);
		}
	}

	void placeWorldQueue(World world, Long2ObjectOpenHashMap<BlockState[][][]> worldQueue, @Nullable Consumer<World> onComplete, boolean immediate) {
		if (worldQueue.isEmpty()) {
			log.debug("Queue for  " + world.getName() + " was empty!");
			worldQueues.remove(world, worldQueue);
			return;
		}

		long start = System.nanoTime();

		AtomicInteger placedChunks = new AtomicInteger();
		AtomicInteger placed = new AtomicInteger();

		int chunkCount = worldQueue.size();

		log.debug("Queued " + chunkCount + " chunks for " + world.getName());

		for (Map.Entry<Long, BlockState[][][]> entry : worldQueue.long2ObjectEntrySet()) {
			long chunkKey = entry.getKey();
			BlockState[][][] blocks = entry.getValue();

			actuallyPlaceChunk(world, onComplete, start, placedChunks, placed, chunkCount, chunkKey, blocks, immediate);
		}

		if (worldQueues.remove(world, worldQueue)) {
			worldQueue.clear();
		}
	}

	private void actuallyPlaceChunk(World world, @Nullable Consumer<World> onComplete, long start, AtomicInteger placedChunks,
									AtomicInteger placed, int chunkCount, long chunkKey, BlockState[][][] blocks, boolean immediate) {
		int cx = chunkKeyX(chunkKey);
		int cz = chunkKeyZ(chunkKey);

		boolean isLoaded = world.isChunkLoaded(cx, cz);

		if (!isLoaded && !immediate) {
			world.getChunkAtAsync(cx, cz).thenAccept(chunk -> {
				actuallyPlaceChunk(world, onComplete, start, placedChunks, placed, chunkCount, blocks, cx, cz, false, chunk);
			});
			return;
		}

		org.bukkit.Chunk chunk = world.getChunkAt(cx, cz);
		actuallyPlaceChunk(world, onComplete, start, placedChunks, placed, chunkCount, blocks, cx, cz, isLoaded, chunk);
	}

	private static final boolean ignoreOldData = true; // if false, client will recalculate lighting based on old/new chunk data

	private void updateHeightMap(@Nullable Heightmap heightMap, int x, int y, int z, BlockState iBlockData) {
		if (heightMap != null) {
			heightMap.update(x & 15, y, z & 15, iBlockData);
		}
	}

	private void actuallyPlaceChunk(World world, @Nullable Consumer<World> onComplete, long start,
									AtomicInteger placedChunks, AtomicInteger placed, int chunkCount,
									BlockState[][][] blocks, int cx, int cz, boolean wasLoaded, org.bukkit.Chunk chunk) {
		LevelChunk nmsChunk = ((CraftChunk) chunk).getHandle();
		ServerLevel nmsWorld = nmsChunk.level;

		LevelChunkSection[] sections = nmsChunk.getSections();

		LevelChunkSection section = null;

		int localPlaced = 0;

		int bitmask = 0; // used for the player chunk update thing to let it know which chunks to update

		Heightmap motionBlocking = nmsChunk.heightMap.get(Heightmap.Type.MOTION_BLOCKING);
		Heightmap motionBlockingNoLeaves = nmsChunk.heightMap.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
		Heightmap oceanFloor = nmsChunk.heightMap.get(Heightmap.Type.OCEAN_FLOOR);
		Heightmap worldSurface = nmsChunk.heightMap.get(Heightmap.Type.WORLD_SURFACE);

		for (int y = 0; y < blocks.length; y++) {
			int sectionY = y >> 4;

			if (section == null || sectionY != section.getYPosition()) {
				section = sections[sectionY];

				if (section == null) {
					section = new LevelChunkSection(sectionY << 4, nmsChunk, nmsWorld, true);
					sections[sectionY] = section;
				}
			}

			BlockState[][] xBlocks = blocks[y];

			for (int x = 0; x < xBlocks.length; x++) {
				BlockState[] zBlocks = xBlocks[x];

				for (int z = 0; z < zBlocks.length; z++) {
					BlockState newData = zBlocks[z];

					if (newData == null) {
						continue;
					}

					BlockState oldData = section.getType(x, y & 15, z);

					if (oldData.getBlock() instanceof BaseEntityBlock && oldData.getBlock() != newData.getBlock()) {
						BlockPos pos = nmsChunk.getPos().asPosition().add(x, y, z);
						nmsWorld.removeTileEntity(pos);
					}

					section.setType(x, y & 15, z, newData);
					updateHeightMap(motionBlocking, x, y, z, newData);
					updateHeightMap(motionBlockingNoLeaves, x, y, z, newData);
					updateHeightMap(oceanFloor, x, y, z, newData);
					updateHeightMap(worldSurface, x, y, z, newData);
					localPlaced++;
				}
			}

			bitmask = bitmask | (1 << sectionY); // update the bitmask to include this section
		}

		relight(world, cx, cz, nmsWorld);
		sendChunkPacket(nmsChunk, bitmask);

		nmsChunk.setNeedsSaving(true);

		if (!wasLoaded) {
			world.unloadChunkRequest(cx, cz);
		}

		int placedNow = placed.addAndGet(localPlaced);
		int placedChunksNow = placedChunks.incrementAndGet();

		if (placedChunksNow == chunkCount) {
			long elapsed = System.nanoTime() - start;
			long elapsedMs = TimeUnit.NANOSECONDS.toMillis(elapsed);
			log.debug(" ===> Placed " + placed + " blocks in " + elapsedMs + "ms");

			if (onComplete != null) {
				onComplete.accept(world);
			}
		} else {
			log.debug("Placed " + placedNow + " blocks and " + placedChunksNow + "/" + chunkCount + " chunks ");
		}
	}

	private void relight(World world, int cx, int cz, ServerLevel nmsWorld) {
		LevelLightEngine lightEngine = nmsWorld.getChunkProvider().getLightEngine();
		lightEngine.b(new ChunkCoordIntPair(cx, cz), world.getEnvironment() == World.Environment.NORMAL);
	}

	private void sendChunkPacket(LevelChunk nmsChunk, int bitmask) {
		PlayerChunk playerChunk = nmsChunk.playerChunk;
		if (playerChunk == null) {
			return;
		}
		PacketPlayOutMapChunk packet = new PacketPlayOutMapChunk(nmsChunk, bitmask);
		playerChunk.sendPacketToTrackedPlayers(packet, false);
	}
}
