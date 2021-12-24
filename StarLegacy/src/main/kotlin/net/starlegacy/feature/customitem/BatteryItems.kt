package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.type.BatteryItem
import net.starlegacy.util.SLTextStyle
import net.starlegacy.util.stripColor
import org.bukkit.Material

object BatteryItems {

	private fun registerBattery(name: String, model: Int, power: Int): BatteryItem {
		val item = BatteryItem(
			id = "battery_${name.stripColor().lowercase().replace(" ", "_")}",
			displayName = name,
			material = Material.APPLE,
			model = model,
			maxPower = power,
		)
		CustomItemManager.register(item)
		return item
	}

	fun register() {
		registerBattery("Battery ${SLTextStyle.RED}A", 7, 500)
		registerBattery("Battery ${SLTextStyle.GREEN}M", 8, 1000)
		registerBattery("Battery ${SLTextStyle.GOLD}G", 9, 2000)
	}

}

