package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.CustomItems.Companion.recipeChoice
import net.starlegacy.feature.customitem.CustomItems.Companion.registerShapedRecipe
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
		val items = mapOf(
			'*' to recipeChoice(CustomItems["titanium"]!!),
			'b' to recipeChoice(CustomItems["battery_g"]!!)
		)
		val helmet = registerPowerArmor("Helmet", 1, 50000, Material.LEATHER_HELMET)
		registerShapedRecipe(helmet.id, helmet.getItem(), "*b*", "* *", ingredients = items)
		val chestplate = registerPowerArmor("Chestplate", 1, 50000, Material.LEATHER_CHESTPLATE)
		registerShapedRecipe(chestplate.id, chestplate.getItem(), "* *", "*b*", "***", ingredients = items)
		val leggings = registerPowerArmor("Leggings", 1, 50000, Material.LEATHER_LEGGINGS)
		registerShapedRecipe(leggings.id, leggings.getItem(), "*b*", "* *", "* *", ingredients = items)
		val boots = registerPowerArmor("Boots", 1, 50000, Material.LEATHER_BOOTS)
		registerShapedRecipe(boots.id, boots.getItem(), "* *", "*b*", ingredients = items)
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