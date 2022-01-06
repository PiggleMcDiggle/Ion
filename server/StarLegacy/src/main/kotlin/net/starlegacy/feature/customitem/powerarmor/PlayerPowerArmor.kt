package net.starlegacy.feature.customitem.powerarmor

import net.starlegacy.StarLegacy.Companion.PLUGIN
import net.starlegacy.feature.customitem.powerarmor.modules.PowerArmorModule
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType


val Player.isWearingPowerArmor: Boolean
	// True if the player is wearing a full set of power armor.
	get() = inventory.helmet?.isPowerArmor ?: false &&
			inventory.chestplate?.isPowerArmor ?: false &&
			inventory.leggings?.isPowerArmor ?: false &&
			inventory.boots?.isPowerArmor ?: false



var Player.armorModules = mutableSetOf<PowerArmorModule>()
	// The player's currently equipped modules.
	get() {
		// Load the player's modules from their PersistentDataContainer
		val moduleCSV = persistentDataContainer.get(
			NamespacedKey(PLUGIN, "equipped-power-armor-modules"),
			PersistentDataType.STRING
		) ?: return mutableSetOf<PowerArmorModule>()
		val moduleNames = moduleCSV.split(",")
		val modules = mutableSetOf<PowerArmorModule>()
		moduleNames.forEach {
			val module = PowerArmorManager.getModuleFromName(it)
			if (module != null) {
				modules.add(module)
			}
		}
		return modules
	}
	set(value) {
		// Save the player's modules to their PersistentDataContainer
		var moduleCSV = "" // Comma separated values of all the module names

		value.forEach {
			moduleCSV += it.name + ","
		}
		player.persistentDataContainer.set(
			NamespacedKey(PLUGIN, "equipped-power-armor-modules"),
			PersistentDataType.STRING,
			moduleCSV
		)
	}

var Player.armorPower: Int
	// The current power of the player's armor. Shared between all armor pieces.
	get() = persistentDataContainer.get(
			NamespacedKey(PLUGIN, "power-armor-power"),
			PersistentDataType.INTEGER) ?: 0

	set(value) {
		var newPower = value // can't modify val
		if (newPower > PowerArmorManager.maxPower) newPower = PowerArmorManager.maxPower
		persistentDataContainer.set(
			NamespacedKey(PLUGIN, "power-armor-power"),
			PersistentDataType.INTEGER,
			newPower
		)
	}

val Player.armorModuleWeight: Int
	// The player's total combined module weight
	get() {
		var weight = 0
		armorModules.forEach {
			weight += it.weight
		}
		return weight
	}


var Player.armorEnabled: Boolean
// Whether the player has enabled power armor in the GUI
	get() = this.persistentDataContainer.get(
		NamespacedKey(PLUGIN, "power-armor-enabled"),
		PersistentDataType.INTEGER) == 1

	set(value) {
		persistentDataContainer.set(
			NamespacedKey(PLUGIN, "power-armor-enabled"),
			PersistentDataType.INTEGER,
			if (value) 1 else 0
		)
	}

fun Player.addArmorModule(module: PowerArmorModule) {
	// Just makes it easier to add a module, so you don't have to do
	// val modules = PlayerArmor(player).modules
	// modules.add(module)
	// PlayerArmor(player).modules = modules
	// Which is very verbose and generally terrible.
	armorModules = (armorModules + module).toMutableSet()
}

fun Player.removeArmorModule(module: PowerArmorModule) {
	// Same reason as addArmorModule
	module.disableModule(this)
	armorModules = (armorModules - module).toMutableSet()
}
