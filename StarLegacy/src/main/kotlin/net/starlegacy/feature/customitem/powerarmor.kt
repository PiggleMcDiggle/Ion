package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.type.PowerArmorItem
import net.starlegacy.feature.customitem.type.PowerModuleItem
import org.bukkit.Material


object PowerArmorItems {
	private fun registerPowerArmor(piece: String, model: Int, maxPower: Int, mat: Material): PowerArmorItem {
		val item = PowerArmorItem(
			id = "power_armor_${piece.lowercase().replace(" ", "_")}",
			displayName = "Power $piece",
			material = mat,
			model = model,
			maxPower = maxPower
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		registerPowerArmor("Helmet", 1, 50000, Material.LEATHER_HELMET)
		registerPowerArmor("Chestplate", 1, 50000, Material.LEATHER_CHESTPLATE)
		registerPowerArmor("Leggings", 1, 50000, Material.LEATHER_LEGGINGS)
		registerPowerArmor("Boots", 1, 50000, Material.LEATHER_BOOTS)
	}
}

object PowerModuleItems {
	private fun registerModuleItem(type: String, typeName: String, model: Int): PowerModuleItem {
		val item = PowerModuleItem(
			id = "power_module_$type",
			displayName = "$typeName Module",
			material = Material.FLINT_AND_STEEL,
			model = model,
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		registerModuleItem("shock_absorbing", "Shock Absorbing", 1)
		registerModuleItem("speed_boosting", "Speed Boosting", 2)
		registerModuleItem("rocket_boosting", "Rocket Boosting", 3)
		registerModuleItem("night_vision", "Night Vision", 4)
		registerModuleItem("environment", "Environment", 5)
		registerModuleItem("pressure_field", "Pressure Field", 6)
	}
}