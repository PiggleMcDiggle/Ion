package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.CustomItems.Companion.recipeChoice
import net.starlegacy.feature.customitem.CustomItems.Companion.registerShapedRecipe
import net.starlegacy.feature.customitem.type.DetonatorItem
import net.starlegacy.feature.customitem.type.GenericCustomItem
import net.starlegacy.util.Tasks
import org.bukkit.ChatColor
import org.bukkit.Material

object MiscItems {

	fun register() {
		// Detonators
		val detonator = CustomItems.register(
			DetonatorItem(
				id = "detonator",
				displayName = "${ChatColor.RED}Thermal${ChatColor.GRAY} Detonator",
				material = Material.SHEARS,
				model = 1
			)
		)
		Tasks.syncDelay(1){
			registerShapedRecipe(detonator.id, detonator.getItem(), " r ", "tut", " t ", ingredients = mapOf(
				'r' to recipeChoice(Material.REDSTONE),
				't' to recipeChoice(CustomItems["titanium"]!!),
				'u' to recipeChoice(CustomItems["uranium"]!!)))
		}
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