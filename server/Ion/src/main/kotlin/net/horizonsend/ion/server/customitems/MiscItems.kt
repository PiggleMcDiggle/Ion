package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.recipeChoice
import net.horizonsend.ion.server.customitems.CustomItems.Companion.registerShapedRecipe
import net.starlegacy.feature.customitem.type.DetonatorItem
import net.starlegacy.util.Tasks
import org.bukkit.ChatColor.GRAY
import org.bukkit.ChatColor.RED
import org.bukkit.Material.REDSTONE
import org.bukkit.Material.SHEARS

object MiscItems {

	fun register() {
		// region Detonators
		val detonator = CustomItems.register(
			DetonatorItem(
				id = "detonator",
				displayName = "${RED}Thermal${GRAY} Detonator",
				material = SHEARS,
				model = 1
			)
		)
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				detonator.id, detonator.getItem(), " r ", "tut", " t ", ingredients = mapOf(
					'r' to recipeChoice(REDSTONE),
					't' to recipeChoice(CustomItems["titanium"]!!),
					'u' to recipeChoice(CustomItems["uranium"]!!)
				)
			)
		}
		// endregion
	}
}