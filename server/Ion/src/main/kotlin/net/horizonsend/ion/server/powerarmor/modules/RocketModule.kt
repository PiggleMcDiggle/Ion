package net.horizonsend.ion.server.powerarmor.modules

import net.horizonsend.ion.server.customitems.types.CustomItem
import net.horizonsend.ion.server.powerarmor.ArmorActivatorRunnable
import net.horizonsend.ion.server.powerarmor.armorPower
import net.starlegacy.PLUGIN
import org.bukkit.Particle.SMOKE_NORMAL
import org.bukkit.Sound.BLOCK_FIRE_AMBIENT
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent
import java.util.UUID

/**
 * Implements a rocket boosting armor module
 */
class RocketModule(override val weight: Int, override val customItem: CustomItem) : PowerArmorModule(), Listener {
	init {
		PLUGIN.server.pluginManager.registerEvents(this, PLUGIN)
	}

	/**
	 * Map of Player to whether they are currently using rockets.
	 */
	val players = mutableMapOf<UUID, Boolean>()

	override fun tickModule(player: Player) {
		if (players[player.uniqueId] == true) {
			if (player.uniqueId !in ArmorActivatorRunnable.activatedPlayers) {
				players[player.uniqueId] = false
				player.isGliding = false
				return
			}
			if (player.isOnGround) {
				players[player.uniqueId] = false
				player.isGliding = false
				return
			}

			player.armorPower -= 5
			player.isGliding = true
			player.world.playSound(player.location, BLOCK_FIRE_AMBIENT, 1.0f, 2.0f)
			player.velocity = player.velocity.midpoint(player.location.direction.multiply(0.6))
			player.world.spawnParticle(SMOKE_NORMAL, player.location, 5)
		}
	}

	@EventHandler
	fun onToggleRocket(event: PlayerToggleSneakEvent) {
		if (event.player.uniqueId in ArmorActivatorRunnable.activatedPlayers) {
			players[event.player.uniqueId] = !(players[event.player.uniqueId] ?: true)
		}
	}
}