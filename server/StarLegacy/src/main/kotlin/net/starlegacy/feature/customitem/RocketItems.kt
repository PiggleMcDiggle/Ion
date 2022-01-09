package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.type.GenericCustomItem
import net.starlegacy.util.Tasks
import org.bukkit.Material

object RocketItems {
	fun register() {
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
		Tasks.syncDelay(1) {
			CustomItems.registerShapelessRecipe(
				rocket.id,
				rocket.getItem(),
				CustomItems.recipeChoice(base),
				CustomItems.recipeChoice(warhead)
			)
			CustomItems.registerShapedRecipe(
				base.id, base.getItem(3), "t t", "tht", "tgt", ingredients = mapOf(
					't' to CustomItems.recipeChoice(CustomItems["titanium"]!!),
					'h' to CustomItems.recipeChoice(CustomItems["gas_canister_helium"]!!),
					'g' to CustomItems.recipeChoice(Material.HOPPER),
				)
			)

			CustomItems.registerShapedRecipe(
				warhead.id, warhead.getItem(3), " a ", "aoa", " a ", ingredients = mapOf(
					'a' to CustomItems.recipeChoice(CustomItems["aluminum"]!!),
					'o' to CustomItems.recipeChoice(CustomItems["diamond_block"]!!)
				)
			)
		}
	}
}