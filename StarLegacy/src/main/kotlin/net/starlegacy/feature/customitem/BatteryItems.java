package net.starlegacy.feature.customitem

import org.bukkit.Material;

class BatteryItems {
	companion object {
		private fun registerBattery(name: String, model: Int, power: Int): BatteryItem{
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
			registerBattery("Battery ${RED}A", 7, 500)
			registerBattery("Battery ${GREEN}M", 8, 1000)
			registerBattery("Battery ${GOLD}G", 9, 2000)
		}
	}
}

