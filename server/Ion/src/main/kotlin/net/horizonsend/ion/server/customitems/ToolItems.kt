package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.recipeChoice
import net.horizonsend.ion.server.customitems.CustomItems.Companion.registerShapedRecipe
import net.horizonsend.ion.server.customitems.types.DrillItem
import net.starlegacy.util.Tasks
import org.bukkit.Material

object ToolItems {
	fun register() {
		val drill =
			CustomItems.register(DrillItem("power_tool_drill", 1, "Power Drill", Material.DIAMOND_PICKAXE, 50000))
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				drill.id, drill.getItem(), "i  ", " bt", " ts", ingredients = mapOf(
					'i' to recipeChoice(Material.IRON_INGOT),
					'b' to recipeChoice(CustomItems["battery_m"]!!),
					't' to recipeChoice(CustomItems["titanium"]!!),
					's' to recipeChoice(Material.STICK)
				)
			)
		}
	}
}