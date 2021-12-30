package net.starlegacy.feature.customitem.powerarmor

import net.starlegacy.feature.customitem.CustomItems
import net.starlegacy.feature.customitem.type.PowerModuleItem
import org.bukkit.inventory.ItemStack

enum class PowerArmorModule(
	private val customItem: PowerModuleItem,
	vararg compatibleTypes: PowerArmorType
) {
	ROCKET_BOOSTING(CustomItems["power_module_rocket_boosting"] as PowerModuleItem, PowerArmorType.BOOTS),
	SPEED_BOOSTING(CustomItems["power_module_speed_boosting"] as PowerModuleItem, PowerArmorType.LEGGINGS),
	SHOCK_ABSORBING(CustomItems["power_module_shock_absorbing"] as PowerModuleItem, PowerArmorType.CHESTPLATE),
	NIGHT_VISION(CustomItems["power_module_night_vision"] as PowerModuleItem, PowerArmorType.HELMET),
	PRESSURE_FIELD(CustomItems["power_module_pressure_field"] as PowerModuleItem, PowerArmorType.HELMET),
	ENVIRONMENT(CustomItems["power_module_environment"] as PowerModuleItem, PowerArmorType.HELMET);

	private val compatibleTypes = compatibleTypes.toSet()

	fun isCompatible(type: PowerArmorType?): Boolean {
		return type != null && compatibleTypes.contains(type)
	}

	companion object {
		private val customitemMap = values().associateBy { it.customItem }
		private val nameMap = values().associateBy { it.name }

		operator fun get(item: ItemStack?): PowerArmorModule? {
			return customitemMap[CustomItems[item]]
		}

		operator fun get(name: String?): PowerArmorModule? {
			return nameMap[name?.uppercase()]
		}
	}
}
