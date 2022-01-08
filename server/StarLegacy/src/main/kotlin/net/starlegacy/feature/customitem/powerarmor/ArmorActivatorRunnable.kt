package net.starlegacy.feature.customitem.powerarmor

import org.bukkit.Bukkit.getServer
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class ArmorActivatorRunnable : BukkitRunnable() {

	companion object {
		var activatedPlayers = mutableSetOf<UUID>()
	}

	override fun run() {
		// Iterate through all the players and activate their armor modules
		// if they're wearing a full set of power armor and have power left.
		// Disable them otherwise
		getServer().onlinePlayers.forEach { player ->
			activatedPlayers = mutableSetOf<UUID>() // purging it is easier than handling disconnects and such
			player.sendMessage("\n \n \n \n \n \n \nEnabled: ${player.armorEnabled}\nWearing: ${player.isWearingPowerArmor}\nPower: ${player.armorPower}/${maxArmorPower}\nWeight: ${player.armorModuleWeight}/$maxModuleWeight\n")
			if (player.isWearingPowerArmor && player.armorEnabled && player.armorPower > 0 && player.armorModuleWeight <= maxModuleWeight) {
				activatedPlayers.add(player.uniqueId)
				player.armorModules.forEach { module ->
					module.tickModule(player)
					player.sendMessage("Ticked module: ${module.customItem.id}")
				}
			} else {
				player.armorModules.forEach { module -> module.disableModule(player) }
			}
		}
	}
}