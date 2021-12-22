package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.type.CustomItem
import org.bukkit.Material

class PlanetIcons {
	companion object {
		private fun registerPlanetIcon(name: String, model: Int): CustomItem {
			val item = CustomItemManager.makeGenericItem(
				id = "planet_icon_${name.lowercase().replace(" ", "_")}",
				name = name,
				material = Material.APPLE,
				modelData = model
			)
			CustomItemManager.register(item)
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
}