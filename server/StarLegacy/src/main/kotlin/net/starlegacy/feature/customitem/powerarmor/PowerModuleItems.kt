package net.starlegacy.feature.customitem.powerarmor

import net.starlegacy.feature.customitem.CustomItems
import net.starlegacy.feature.customitem.powerarmor.modules.PotionEffectModule
import net.starlegacy.feature.customitem.powerarmor.modules.RocketModule
import net.starlegacy.feature.customitem.powerarmor.modules.SpeedModule
import net.starlegacy.feature.customitem.type.GenericCustomItem
import net.starlegacy.util.Tasks
import org.bukkit.Material
import org.bukkit.potion.PotionEffectType


object PowerModuleItems {
	private fun registerModuleItem(type: String, typeName: String, model: Int, craft: String): GenericCustomItem {
		val item = GenericCustomItem(
			id = "power_module_$type",
			displayName = "$typeName Module",
			material = Material.FLINT_AND_STEEL,
			model = model,
		)
		CustomItems.register(item)
		Tasks.syncDelay(1) {
			CustomItems.registerShapedRecipe(
				item.id, item.getItem(), "aga", "g*g", "aga", ingredients = mapOf(
					'a' to CustomItems.recipeChoice(CustomItems["aluminum"]!!),
					'g' to CustomItems.recipeChoice(Material.GLASS_PANE),
					'*' to CustomItems.recipeChoice(CustomItems.itemStackFromId(craft)!!)
				)
			)
		}

		return item
	}

	fun register() {
		powerArmorModules.add(
			RocketModule(
				3,
				registerModuleItem("rocket_boosting", "Rocket Boosting", 3, "firework_rocket")
			)
		)
		powerArmorModules.add(
			SpeedModule(
				3,
				registerModuleItem("speed_boosting", "Speed Boosting", 2, "feather"),
				effectMultiplier = 1,
				effectDuration = 2,
				power = 1,
			)
		)
		powerArmorModules.add(
			PotionEffectModule(
				1,
				registerModuleItem("night_vision", "Night Vision", 4, "spider_eye"),
				PotionEffectType.NIGHT_VISION,
				0,
				300,
				0
			)
		)
	}
}
