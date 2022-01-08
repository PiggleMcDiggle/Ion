package net.starlegacy.feature.customitem.powerarmor.modules

import net.starlegacy.PLUGIN
import net.starlegacy.feature.customitem.powerarmor.ArmorActivatorRunnable
import net.starlegacy.feature.customitem.powerarmor.armorPower
import net.starlegacy.feature.customitem.type.CustomItem
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent
import java.util.UUID

class RocketModule(override val weight: Int, override val customItem: CustomItem) : PowerArmorModule(), Listener {
	init {
		PLUGIN.server.pluginManager.registerEvents(this, PLUGIN)
	}

	val players = mutableMapOf<UUID, Boolean>()

	override fun tickModule(player: Player) {
		if (players[player.uniqueId] == true) {
			if (player.uniqueId !in ArmorActivatorRunnable.activatedPlayers) {
				players[player.uniqueId] = false
				player.isGliding = false
				return
			}
			if (player.isOnGround) {
				player.isGliding = false
			}

			player.armorPower -= 5
			player.isGliding = true
			player.world.playSound(player.location, Sound.BLOCK_FIRE_AMBIENT, 1.0f, 2.0f)
			player.velocity = player.velocity.midpoint(player.location.direction.multiply(0.6))
			player.world.spawnParticle(Particle.SMOKE_NORMAL, player.location, 5)
		}
	}

	@EventHandler
	fun onToggleRocket(event: PlayerToggleSneakEvent) {
		if (event.player.uniqueId in ArmorActivatorRunnable.activatedPlayers) {
			players[event.player.uniqueId] = !(players[event.player.uniqueId] ?: true)
		}
	}
}