package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.recipeChoice
import org.bukkit.Material.*
import org.bukkit.inventory.ItemStack

object MiscRecipes {
	fun register() {
		CustomItems.registerShapelessRecipe(
			"wire",
			ItemStack(END_ROD, 16),
			recipeChoice(COPPER_INGOT),
			recipeChoice(COPPER_INGOT),
			recipeChoice(COPPER_INGOT)
		)
		CustomItems.registerShapelessRecipe(
			"sea_lantern",
			ItemStack(SEA_LANTERN, 1),
			recipeChoice(PRISMARINE_CRYSTALS),
			recipeChoice(PRISMARINE_CRYSTALS),
			recipeChoice(PRISMARINE_CRYSTALS),
			recipeChoice(PRISMARINE_CRYSTALS)
		)
		CustomItems.registerShapedRecipe(
			"end_portal_frame",
			ItemStack(END_PORTAL_FRAME, 1),
			"wow", "sss",
			ingredients = mapOf(
				'w' to recipeChoice(WARPED_PLANKS),
				'o' to recipeChoice(ENDER_PEARL),
				's' to recipeChoice(END_STONE)
			)
		)
	}
}