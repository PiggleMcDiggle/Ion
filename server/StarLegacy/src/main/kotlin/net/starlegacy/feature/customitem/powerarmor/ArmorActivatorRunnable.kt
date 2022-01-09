package net.starlegacy.feature.customitem.powerarmor

import org.bukkit.Bukkit.getServer
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID

class ArmorActivatorRunnable : BukkitRunnable() {

	companion object

	var activatedPlayers = mutableSetOf<UUID>()

	override fun run() {
		// Iterate through all the players and activate their armor modules
		// if they're wearing a full set of power armor and have power left.
		// Disable them otherwise

		// Not sure how good this is performance-wise, but it isn't any worse than the old system
		activatedPlayers = mutableSetOf<UUID>() // purging it is easier than handling disconnects and such
		getServer().onlinePlayers.forEach { player ->
			if (player.isWearingPowerArmor && player.armorEnabled && player.armorPower > 0 && player.armorModuleWeight <= maxModuleWeight) {
				activatedPlayers.add(player.uniqueId)
				player.armorModules.forEach { module ->
					module.tickModule(player)
				}
			} else player.armorModules.forEach { module -> module.disableModule(player) }
		}
	}
}