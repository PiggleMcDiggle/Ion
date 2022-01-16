package net.horizonsend.ion.server.powerarmor.modules

import net.starlegacy.PLUGIN
import net.horizonsend.ion.server.powerarmor.armorPower
import net.horizonsend.ion.server.customitems.types.CustomItem
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import java.time.Instant
import java.util.UUID

class SpeedModule(
	override val weight: Int,
	override val customItem: CustomItem,
	override val effectMultiplier: Int,
	override val effectDuration: Int,
	val power: Int
) :
	PotionEffectModule(weight, customItem, PotionEffectType.SPEED, effectMultiplier, effectDuration, 0), Listener {
	// Not using a PotionEffectModule so that it doesn't drain power while standing still
	init {
		PLUGIN.server.pluginManager.registerEvents(this, PLUGIN)
	}

	val lastMoved = mutableMapOf<UUID, Long>()

	override fun tickModule(player: Player) {
		super.tickModule(player)
		if (hasMovedInLastSecond(player)) {
			player.armorPower -= power
		}
	}

	// onMove and hasMovedInLastSecond copied from original SL armor
	@EventHandler
	fun onMove(event: PlayerMoveEvent) {
		lastMoved[event.player.uniqueId] = System.currentTimeMillis()
	}
	private fun hasMovedInLastSecond(player: Player): Boolean {
		return lastMoved.containsKey(player.uniqueId) && System.currentTimeMillis() - (lastMoved[player.uniqueId]
			?: 0) < 1000
	}
}