package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.type.DetonatorItem
import org.bukkit.ChatColor
import org.bukkit.Material

class MiscItems {
	companion object {
		fun register() {
			// Detonators
			CustomItemManager.register(
				DetonatorItem(
				id = "detonator",
				displayName = "${ChatColor.RED}Thermal${ChatColor.GRAY} Detonator",
				material = Material.SHEARS,
				model = 1
			)
			)
		}
	}
}