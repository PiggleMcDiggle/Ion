package net.starlegacy.feature.starship.movement

import java.util.concurrent.CompletableFuture
import kotlin.math.max
import kotlin.math.min
import net.starlegacy.feature.starship.active.ActivePlayerStarship
import net.starlegacy.feature.starship.active.ActiveStarship
import net.starlegacy.util.ConnectionUtils
import net.starlegacy.util.NMSBlockData
import net.starlegacy.util.Tasks
import net.starlegacy.util.add
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class TranslateMovement(starship: ActiveStarship, val dx: Int, val dy: Int, val dz: Int, newWorld: World? = null) :
	StarshipMovement(starship, newWorld) {
	companion object {
		fun loadChunksAndMove(
			starship: ActiveStarship,
			dx: Int,
			dy: Int,
			dz: Int,
			newWorld: World? = null
		): CompletableFuture<Boolean> {
			val world = newWorld ?: starship.world

			val toLoad = this.getChunkLoadTasks(starship, world, dx, dz)

			return CompletableFuture.allOf(*toLoad.toTypedArray()).thenCompose {
				Tasks.checkMainThread()
				return@thenCompose starship.moveAsync(TranslateMovement(starship, dx, dy, dz, newWorld))
			}
		}

		private fun getChunkLoadTasks(
			starship: ActiveStarship,
			world: World,
			dx: Int,
			dz: Int
		): MutableSet<CompletableFuture<Chunk>> {
			val newMinChunkX = min(starship.min.x + dx, starship.max.x + dx) shr 4
			val newMaxChunkX = max(starship.min.x + dx, starship.max.x + dx) shr 4
			val cxRange = newMinChunkX..newMaxChunkX

			val newMinChunkZ = min(starship.min.z + dz, starship.max.z + dz) shr 4
			val newMaxChunkZ = max(starship.min.z + dz, starship.max.z + dz) shr 4
			val czRange = newMinChunkZ..newMaxChunkZ

			val toLoad = mutableSetOf<CompletableFuture<Chunk>>()

			for (cx in cxRange) {
				for (cz in czRange) {
					val chunkFuture = world.getChunkAtAsyncUrgently(cx, cz)
					toLoad.add(chunkFuture)
				}
			}

			return toLoad
		}
	}

	override fun blockDataTransform(blockData: NMSBlockData): NMSBlockData = blockData

	override fun displaceX(oldX: Int, oldZ: Int): Int = oldX + dx

	override fun displaceY(oldY: Int): Int = oldY + dy

	override fun displaceZ(oldZ: Int, oldX: Int): Int = oldZ + dz

	override fun displaceLocation(oldLocation: Location): Location {
		val newLocation = oldLocation.clone().add(dx, dy, dz)
		if (newWorld != null) {
			newLocation.world = newWorld
		}
		return newLocation
	}

	override fun movePassenger(passenger: Entity) {
		val location = passenger.location.clone()
		location.add(dx, dy, dz)
		if (newWorld != null) {
			location.world = newWorld
		}

		if (passenger !is Player || newWorld != null) {
			passenger.teleport(location)
			return
		}

		if ((starship as? ActivePlayerStarship)?.isDirectControlEnabled == true) {
			ConnectionUtils.move(passenger, location, dx.toDouble(), dy.toDouble(), dz.toDouble())
			return
		}

		ConnectionUtils.teleport(passenger, location)
	}

	override fun onComplete() {}
}
