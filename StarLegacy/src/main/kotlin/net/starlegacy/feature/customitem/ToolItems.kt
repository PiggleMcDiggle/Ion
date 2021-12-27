package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.type.DrillItem
import org.bukkit.Material

object ToolItems {
	fun register() {
		CustomItems.register(DrillItem("power_tool_drill", 1, "Power Drill", Material.DIAMOND_PICKAXE, 50000))
	}
}