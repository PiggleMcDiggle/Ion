package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.types.CustomItem
import net.horizonsend.ion.server.customitems.types.GenericCustomItem
import org.bukkit.Material

object PlanetIcons {

	private fun registerPlanetIcon(name: String, model: Int): CustomItem {
		val item = GenericCustomItem(
			id = "planet_icon_${name.lowercase().replace(" ", "_")}",
			displayName = name,
			material = Material.APPLE,
			model = model
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		registerPlanetIcon("Aecor", 1)
		registerPlanetIcon("Arbusto", 2)
		registerPlanetIcon("Cerus Alpha", 3)
		registerPlanetIcon("Cerus Beta", 4)
		registerPlanetIcon("Collis", 5)
		registerPlanetIcon("Harenum", 6)
		registerPlanetIcon("Koryza", 7)
		registerPlanetIcon("Orcus", 8)
		registerPlanetIcon("Porrus", 9)
		registerPlanetIcon("Quod Canis", 10)
		registerPlanetIcon("Sakaro", 11)
		registerPlanetIcon("Syre", 12)
		registerPlanetIcon("Terram", 13)
		registerPlanetIcon("Titus", 14)
		registerPlanetIcon("Trunkadis", 15)
	}
}