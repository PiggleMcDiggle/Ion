package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.recipeChoice
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object MiscRecipes {
	fun register() {
		CustomItems.registerShapelessRecipe(
			"wire",
			ItemStack(Material.END_ROD, 16),
			recipeChoice(Material.COPPER_INGOT),
			recipeChoice(Material.COPPER_INGOT),
			recipeChoice(Material.COPPER_INGOT)
		)
		CustomItems.registerShapelessRecipe(
			"sea_lantern",
			ItemStack(Material.SEA_LANTERN, 1),
			recipeChoice(Material.PRISMARINE_CRYSTALS),
			recipeChoice(Material.PRISMARINE_CRYSTALS),
			recipeChoice(Material.PRISMARINE_CRYSTALS),
			recipeChoice(Material.PRISMARINE_CRYSTALS)
		)
		CustomItems.registerShapedRecipe(
			"end_portal_frame",
			ItemStack(Material.END_PORTAL_FRAME, 1),
			"wow", "sss",
			ingredients = mapOf(
				'w' to recipeChoice(Material.WARPED_PLANKS),
				'o' to recipeChoice(Material.ENDER_PEARL),
				's' to recipeChoice(Material.END_STONE)
			)
		)
	}
}