package net.horizonsend.ion.server.customitems.types

import net.starlegacy.StarLegacy.Companion.PLUGIN
import net.starlegacy.feature.customitem.blaster.Blasters
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Holds values required for blasters
 */
class BlasterItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	override val maxPower: Int,
	override val maxUses: Int,
	/**
	 * The speed at which the projectile travels
	 */
	val speed: Double,
	/**
	 * The maximum number of blocks the projectile can travel
	 */
	val range: Int,
	/**
	 * The thickness of the projectile's particles
	 */
	val thickness: Double,
	/**
	 * The item cooldown between shots
	 */
	val cooldown: Int,
	/**
	 * The power consumed with each shot
	 */
	val power: Int,
	/**
	 * The damage the projectile deals to entities
	 */
	val damage: Double,
	/**
	 * The sound played when the blaster is fired
	 */
	val sound: String,
	/**
	 * Base pitch for the [sound]
	 */
	val pitchBase: Double,
	/**
	 * Range of the [sound]
	 */
	val pitchRange: Double,
	/**
	 * The power of the explosion when the projectile hits a block
	 */
	val explosionPower: Float? = null
) : BreakablePowerItem(), Listener {

	init {
		PLUGIN.server.pluginManager.registerEvents(this, PLUGIN)
	}

	override fun onLeftClick(event: PlayerInteractEvent) {
		Blasters.fireBlaster(event.player, event.item!!)
	}

	// The rest of this is copied from the old SL BlasterListener.kt

	@EventHandler
	fun onFireBlaster(event: EntityShootBowEvent) {
		val entity = event.entity
		val bow = event.bow ?: return

		if (entity is Player && entity.gameMode == GameMode.SPECTATOR) {
			return
		}

		val blaster = Blasters.getBlaster(bow) ?: return

		event.isCancelled = true
		Blasters.fireBlaster(entity, bow)
	}
	

	@EventHandler
	fun onEntityDamage(event: EntityDamageByEntityEvent) {
		val entity = event.entity
		val damager = event.damager
		if (entity is Monster && (damager is Monster || damager is Projectile && damager.shooter is Monster)) {
			event.isCancelled = true
		}
	}
}