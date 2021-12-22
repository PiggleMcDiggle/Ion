package net.starlegacy.feature.customitem

import org.bukkit.ChatColor
import org.bukkit.Material

class MiscItems {
	companion object {
		fun register() {
			// Detonators
			CustomItemManager.register(CustomItemManager.makeGenericItem(
				id = "detonator",
				name = "${ChatColor.RED}Thermal${ChatColor.GRAY} Detonator",
				material = Material.SHEARS,
				modelData = 1
			))
		}
	}
}