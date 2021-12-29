package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.CustomItems.Companion.recipeChoice
import net.starlegacy.feature.customitem.CustomItems.Companion.registerShapedRecipe
import net.starlegacy.feature.customitem.CustomItems.Companion.registerShapelessRecipe
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
		val base = CustomItems.register(
			GenericCustomItem(
				id = "rocket_base",
				displayName = "Rocket Base",
				material = Material.STICK,
				model = 1
			)
		)
		val warhead = CustomItems.register(
			GenericCustomItem(
				id = "rocket_warhead_oriomium",
				displayName = "Oriomium Warhead",
				material = Material.STICK,
				model = 2
			)
		)
		val rocket = CustomItems.register(
			GenericCustomItem(
				id = "rocket_oriomium",
				displayName = "Oriomium Rocket",
				material = Material.STICK,
				model = 3
			)
		)
		Tasks.syncDelay(1){
			registerShapelessRecipe(
				rocket.id,
				rocket.getItem(),
				recipeChoice(base),
				recipeChoice(warhead)
			)
			registerShapedRecipe(
				base.id, base.getItem(3), "t t", "tht", "tgt", ingredients = mapOf(
					't' to recipeChoice(CustomItems["titanium"]!!),
					'h' to recipeChoice(CustomItems["gas_canister_helium"]!!),
					'g' to recipeChoice(Material.HOPPER),
				)
			)

			registerShapedRecipe(
				warhead.id, warhead.getItem(3), " a ", "aoa", " a ", ingredients = mapOf(
					'a' to recipeChoice(CustomItems["aluminum"]!!),
					'o' to recipeChoice(CustomItems["oriomium_block"]!!)
				)
			)
		}
	}
}