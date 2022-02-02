package net.horizonsend.ion.server.customitems

import org.bukkit.Material.END_PORTAL_FRAME
import org.bukkit.Material.END_ROD
import org.bukkit.Material.SEA_LANTERN
import org.bukkit.inventory.ItemStack

object MiscRecipes {
	fun register() {
		CustomItems.registerShapelessRecipe(
			ItemStack(END_ROD, 16),
			List(3){"copper_ingot"}.toSet()
		)
		CustomItems.registerShapelessRecipe(
			ItemStack(SEA_LANTERN, 1),
			List(4){"prismarine_crystals"}.toSet()
		)
		CustomItems.registerShapedRecipe(
			ItemStack(END_PORTAL_FRAME, 1),
			listOf(
				"warped_planks", "ender_pearl", "warped_planks",
				"end_stone",     "end_stone",   "end_stone",
				null,            null,          null
			)
		)
	}
}