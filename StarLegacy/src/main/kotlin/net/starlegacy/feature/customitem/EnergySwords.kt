package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.type.EnergySwordItem
import org.bukkit.Material;

object EnergySwords {

	private fun registerEnergySword(name: String, model: Int): EnergySwordItem {
		val item = EnergySwordItem(
			id = "energy_sword_${name.lowercase().replace(" ", "_")}",
			displayName = "Energy Sword - $name",
			material = Material.SHIELD,
			model = model
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		registerEnergySword("blue", 1)
		registerEnergySword("red", 2)
		registerEnergySword("yellow", 3)
		registerEnergySword("green", 4)
		registerEnergySword("purple", 5)
		registerEnergySword("orange", 6)
	}

}