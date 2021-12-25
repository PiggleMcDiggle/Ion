package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.type.DetonatorItem
import net.starlegacy.feature.customitem.type.GenericCustomItem
import org.bukkit.ChatColor
import org.bukkit.Material

object MiscItems {

	fun register() {
		// Detonators
		CustomItems.register(
			DetonatorItem(
				id = "detonator",
				displayName = "${ChatColor.RED}Thermal${ChatColor.GRAY} Detonator",
				material = Material.SHEARS,
				model = 1
			)
		)
		CustomItems.register(
			GenericCustomItem(
				id = "rocket_base",
				displayName = "Rocket Base",
				material = Material.STICK,
				model = 1
			)
		)
		CustomItems.register(
			GenericCustomItem(
				id = "rocket_warhead_oriomium",
				displayName = "Oriomium Warhead",
				material = Material.STICK,
				model = 2
			)
		)
		CustomItems.register(
			GenericCustomItem(
				id = "rocket_oriomium",
				displayName = "Oriomium Rocket",
				material = Material.STICK,
				model = 3
			)
		)
	}
}