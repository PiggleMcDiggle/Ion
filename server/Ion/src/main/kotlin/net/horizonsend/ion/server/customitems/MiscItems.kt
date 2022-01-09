package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.recipeChoice
import net.horizonsend.ion.server.customitems.CustomItems.Companion.registerShapedRecipe
import net.starlegacy.feature.customitem.type.DetonatorItem
import net.starlegacy.util.Tasks
import org.bukkit.ChatColor
import org.bukkit.Material

object MiscItems {

	fun register() {
		// region Detonators
		val detonator = CustomItems.register(
			DetonatorItem(
				id = "detonator",
				displayName = "${ChatColor.RED}Thermal${ChatColor.GRAY} Detonator",
				material = Material.SHEARS,
				model = 1
			)
		)
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				detonator.id, detonator.getItem(), " r ", "tut", " t ", ingredients = mapOf(
					'r' to recipeChoice(Material.REDSTONE),
					't' to recipeChoice(CustomItems["titanium"]!!),
					'u' to recipeChoice(CustomItems["uranium"]!!)
				)
			)
		}
		// endregion
	}
}