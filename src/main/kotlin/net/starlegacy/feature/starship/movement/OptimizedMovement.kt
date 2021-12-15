package net.starlegacy.feature.starship.movement

import co.aikar.commands.ConditionFailedException
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket
import net.minecraft.server.level.ChunkHolder
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.StainedGlassBlock
import net.minecraft.world.level.chunk.LevelChunkSection
import net.minecraft.world.level.levelgen.Heightmap
import net.starlegacy.feature.starship.Hangars
import net.starlegacy.feature.starship.active.ActiveStarship
import net.starlegacy.feature.starship.active.ActiveStarships
import net.starlegacy.util.*
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.BlockData
import java.util.*
import java.util.concurrent.ExecutionException

object OptimizedMovement {
	private val passThroughBlocks = listOf(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.SNOW)
		.map { it.createBlockData().nms }
		.toSet()

	private val timing = timing("Starship Movement")

	fun moveStarship(
		starship: ActiveStarship,
		world1: World,
		world2: World,
		oldPositionArray: LongArray,
		newPositionArray: LongArray,
		blockDataTransform: (NMSBlockData) -> NMSBlockData,
		callback: () -> Unit
	) {
		val oldChunkMap = getChunkMap(oldPositionArray)
		val newChunkMap = getChunkMap(newPositionArray)
		val collisionChunkMap = getCollisionChunkMap(oldChunkMap, newChunkMap)

		val n = oldPositionArray.size
		val capturedStates = java.lang.reflect.Array.newInstance(NMSBlockData::class.java, n) as Array<NMSBlockData>
		val capturedTiles = mutableMapOf<Int, NMSTileEntity>()
		val hangars = LinkedList<Long>()

		try {
			Tasks.syncBlocking {
				if (!ActiveStarships.isActive(starship)) {
					return@syncBlocking
				}

				timing.time {
					checkForCollision(world2, collisionChunkMap, hangars, newPositionArray)

					processOldBlocks(
						oldChunkMap,
						world1,
						world2,
						capturedStates,
						capturedTiles
					)

					dissipateHangarBlocks(world2, hangars)

					processNewBlocks(
						newPositionArray,
						newChunkMap,
						world1,
						world2,
						capturedStates,
						capturedTiles,
						blockDataTransform
					)

					callback()

					sendChunkUpdatesToPlayers(world1, world2, oldChunkMap, newChunkMap)
				}
			}
		} catch (e: ExecutionException) {
			throw e.cause ?: e
		}
	}

	private fun checkForCollision(
		world: World,
		collisionChunkMap: ChunkMap,
		hangars: LinkedList<Long>,
		newPositionArray: LongArray
	) {
		for ((chunkKey, sectionMap) in collisionChunkMap) {
			val chunk = world.getChunkAt(chunkKeyX(chunkKey), chunkKeyZ(chunkKey))
			val nmsChunk = chunk.nms

			for ((sectionKey, positionMap) in sectionMap) {
				val section = getChunkSection(nmsChunk, sectionKey)

				for ((blockKey, index) in positionMap) {
					check(newPositionArray[index] == blockKey)

					val x = blockKeyX(blockKey)
					val y = blockKeyY(blockKey)
					val z = blockKeyZ(blockKey)

					val localX = x and 0xF
					val localY = y and 0xF
					val localZ = z and 0xF

					val blockData = section.getBlockState(localX, localY, localZ)

					if (!passThroughBlocks.contains(blockData)) {
						if (!isHangar(blockData)) {
							throw ConditionFailedException("Blocked at $x, $y, $z by `$blockData`!")
						}

						hangars.add(blockKey)
					}
				}
			}
		}
	}

	private fun isHangar(newBlockData: NMSBlockData) = newBlockData.block is StainedGlassBlock

	private fun dissipateHangarBlocks(world2: World, hangars: LinkedList<Long>) {
		for (blockKey in hangars.iterator()) {
			Hangars.dissipateBlock(world2, blockKey)
		}
	}

	private fun processOldBlocks(
		oldChunkMap: ChunkMap,
		world1: World,
		world2: World,
		capturedStates: Array<NMSBlockData>,
		capturedTiles: MutableMap<Int, NMSTileEntity>
	) {
		val lightEngine = world1.nms.lightEngine
		val air = Blocks.AIR.defaultBlockState()

		for ((chunkKey, sectionMap) in oldChunkMap) {
			val chunk = world1.getChunkAt(chunkKeyX(chunkKey), chunkKeyZ(chunkKey))
			val nmsChunk = chunk.nms

			for ((sectionKey, positionMap) in sectionMap) {
				val section = getChunkSection(nmsChunk, sectionKey)

				for ((blockKey, index) in positionMap) {
					val x = blockKeyX(blockKey)
					val y = blockKeyY(blockKey)
					val z = blockKeyZ(blockKey)

					val localX = x and 0xF
					val localY = y and 0xF
					val localZ = z and 0xF

					val type = section.getBlockState(localX, localY, localZ)
					capturedStates[index] = type

					if (type.block is NMSBlockTileEntity) {
						processOldTile(blockKey, chunk, capturedTiles, index, world1, world2)
					}

					section.setBlockState(localX, localY, localZ, air, false)

					lightEngine.checkBlock(NMSBlockPos(x, y, z))
				}
			}

			updateHeightMaps(nmsChunk)
		}
	}

	private fun processNewBlocks(
		newPositionArray: LongArray,
		newChunkMap: ChunkMap,
		world1: World,
		world2: World,
		capturedStates: Array<NMSBlockData>,
		capturedTiles: MutableMap<Int, NMSTileEntity>,
		blockDataTransform: (NMSBlockData) -> NMSBlockData
	) {
		val lightEngine = world2.nms.lightEngine

		for ((chunkKey, sectionMap) in newChunkMap) {
			val chunk = world2.getChunkAt(chunkKeyX(chunkKey), chunkKeyZ(chunkKey))
			val nmsChunk = chunk.nms

			for ((sectionKey, positionMap) in sectionMap) {
				val section = getChunkSection(nmsChunk, sectionKey)

				for ((blockKey, index) in positionMap) {
					val x = blockKeyX(blockKey)
					val y = blockKeyY(blockKey)
					val z = blockKeyZ(blockKey)

					val localX = x and 0xF
					val localY = y and 0xF
					val localZ = z and 0xF

					// TODO: Save hangars
					val data = blockDataTransform(capturedStates[index])
					section.setBlockState(localX, localY, localZ, data, false)
					lightEngine.checkBlock(NMSBlockPos(x, y, z))
				}
			}

			updateHeightMaps(nmsChunk)
		}

		for ((index, tile) in capturedTiles) {
			val blockKey = newPositionArray[index]
			val x = blockKeyX(blockKey)
			val y = blockKeyY(blockKey)
			val z = blockKeyZ(blockKey)

			world2.setBlockData(x, y, z, (tile as BlockData)) // Just do it with bukkit, I don't understand NMS enough to do it with NMS
		}
	}

	private fun getChunkSection(nmsChunk: NMSChunk, sectionY: Int): LevelChunkSection {
		var section = nmsChunk.sections[sectionY]
		if (section == null) {
			section = LevelChunkSection(sectionY, nmsChunk, nmsChunk.level, true)
			nmsChunk.sections[sectionY] = section
		}
		return section
	}

	private fun updateHeightMaps(nmsChunk: NMSChunk) {
		Heightmap.primeHeightmaps(nmsChunk, Heightmap.Types.values().toSet())
	}

	private fun processOldTile(
		blockKey: Long,
		chunk: Chunk,
		capturedTiles: MutableMap<Int, NMSTileEntity>,
		index: Int,
		world1: World,
		world2: World
	) {
		val blockPos = NMSBlockPos(
			blockKeyX(blockKey),
			blockKeyY(blockKey),
			blockKeyZ(blockKey)
		)

		val tile: NMSTileEntity = chunk.nms.getTileEntityImmediately(blockPos) ?: return
		capturedTiles[index] = tile

		if (world1.uid != world2.uid) {
			world1.nms.removeBlockEntity(blockPos)
			return
		}

		chunk.nms.blockEntities.remove(blockPos, tile)
	}

	private fun getChunkMap(positionArray: LongArray): ChunkMap {
		val chunkMap = mutableMapOf<Long, MutableMap<Int, MutableMap<Long, Int>>>()

		for (index in positionArray.indices) {
			val blockKey = positionArray[index]
			val x = blockKeyX(blockKey)
			val y = blockKeyY(blockKey)
			val z = blockKeyZ(blockKey)
			val chunkKey = chunkKey(x shr 4, z shr 4)
			val sectionKey = y shr 4
			val sectionMap = chunkMap.getOrPut(chunkKey) { mutableMapOf() }
			val positionMap = sectionMap.getOrPut(sectionKey) { mutableMapOf() }
			positionMap[blockKey] = index
		}

		return chunkMap
	}

	/* Chunk map containing only positions
	   from the new chunk map that
	   are not in the old chunk map */
	private fun getCollisionChunkMap(oldChunkMap: ChunkMap, newChunkMap: ChunkMap): ChunkMap {
		val chunkMap = mutableMapOf<Long, MutableMap<Int, MutableMap<Long, Int>>>()

		for ((chunkKey, newSectionMap) in newChunkMap) {
			val oldSectionMap = oldChunkMap[chunkKey]

			for ((sectionKey, newPositionMap) in newSectionMap) {
				val oldPositionMap = oldSectionMap?.get(sectionKey)

				for ((blockKey, index) in newPositionMap) {
					if (oldPositionMap?.containsKey(blockKey) == true) {
						continue
					}

					val sectionMap = chunkMap.getOrPut(chunkKey) { mutableMapOf() }
					val positionMap = sectionMap.getOrPut(sectionKey) { mutableMapOf() }
					positionMap[blockKey] = index
				}
			}
		}

		return chunkMap
	}

	private fun sendChunkUpdatesToPlayers(world1: World, world2: World, oldChunkMap: ChunkMap, newChunkMap: ChunkMap) {
		val bitmasks = mutableMapOf<Pair<UUID, Long>, Int>()

		for ((chunkMap, world) in listOf(oldChunkMap to world1.uid, newChunkMap to world2.uid)) {
			for ((chunkKey, sectionMap) in chunkMap) {
				var bitmask = bitmasks.getOrPut(world to chunkKey) { 0 }
				for (sectionY in sectionMap.keys) {
					bitmask = bitmask or (1 shl sectionY)
				}
				bitmasks[world to chunkKey] = bitmask
			}
		}

		for ((key, bitmask: Int) in bitmasks) {
			val (worldID, chunkKey) = key
			val chunk = Bukkit.getWorld(worldID)!!.getChunkAt(chunkKeyX(chunkKey), chunkKeyZ(chunkKey))
			val nmsChunk = chunk.nms
			val playerChunk: ChunkHolder = nmsChunk.playerChunk ?: continue
			val packet = ClientboundLevelChunkPacket(nmsChunk, true)
			playerChunk.broadcast(packet, false)
		}
	}
}

private typealias ChunkMap = Map<Long, Map<Int, Map<Long, Int>>>
