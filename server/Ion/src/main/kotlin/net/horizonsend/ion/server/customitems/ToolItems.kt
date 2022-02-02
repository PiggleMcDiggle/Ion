package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.registerShapedRecipe
import net.horizonsend.ion.server.customitems.types.DrillItem
import net.starlegacy.util.Tasks
import org.bukkit.Material.DIAMOND_PICKAXE

object ToolItems {
	fun register() {
		val drill =
			CustomItems.register(DrillItem("power_tool_drill", 1, "<gold>Power Drill", DIAMOND_PICKAXE, 50000))
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				drill.getItem(),
				listOf(
					"iron_ingot", null,        null,
					null,         "battery_m", "titanium",
					null,         "titanium",  "stick"
				)
			)
		}
	}
}