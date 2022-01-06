package net.starlegacy.feature.customitem.powerarmor

import org.bukkit.Bukkit.getServer
import org.bukkit.scheduler.BukkitRunnable

class ArmorActivatorRunnable : BukkitRunnable() {

	override fun run() {
		// Iterate through all the players and activate their armor modules
		// if they're wearing a full set of power armor and have power left.
		// Disable them otherwise
		getServer().onlinePlayers.forEach { player ->
			if (player.isWearingPowerArmor && player.armorEnabled && player.armorPower > 0 && player.armorModuleWeight <= maxModuleWeight) {
				player.armorModules.forEach { module -> module.enableModule(player) }
			} else {
				player.armorModules.forEach { module -> module.disableModule(player) }
			}
		}
	}
}