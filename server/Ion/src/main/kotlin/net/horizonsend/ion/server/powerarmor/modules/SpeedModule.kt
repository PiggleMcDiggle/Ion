package net.horizonsend.ion.server.powerarmor.modules

import net.horizonsend.ion.server.customitems.types.CustomItem
import net.horizonsend.ion.server.powerarmor.armorPower
import net.starlegacy.PLUGIN
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType.SPEED
import java.util.UUID

/**
 * Implements a speed boosting module.
 * Only draws power when the player is moving
 *
 * @see PotionEffectModule
 */
class SpeedModule(
	override val weight: Int,
	override val customItem: CustomItem,
	override val effectMultiplier: Int,
	override val effectDuration: Int,
	/**
	 * The [Player.armorPower] consumed on a module tick when the player is moving
	 */
	val power: Int
) :
	PotionEffectModule(weight, customItem, SPEED, effectMultiplier, effectDuration, 0), Listener {
	// Not using a PotionEffectModule so that it doesn't drain power while standing still
	init {
		PLUGIN.server.pluginManager.registerEvents(this, PLUGIN)
	}

	private val lastMoved = mutableMapOf<UUID, Long>()

	override fun tickModule(player: Player) {
		super.tickModule(player)
		if (hasMovedInLastSecond(player)) {
			player.armorPower -= power
		}
	}


	@EventHandler
	fun onMove(event: PlayerMoveEvent) {
		lastMoved[event.player.uniqueId] = System.currentTimeMillis()
	}

	/**
	 * Copied from original StarLegacy code
	 *
	 * @return whether [player] has moved in the last second
	 */
	private fun hasMovedInLastSecond(player: Player): Boolean {
		return lastMoved.containsKey(player.uniqueId) && System.currentTimeMillis() - (lastMoved[player.uniqueId]
			?: 0) < 1000
	}
}