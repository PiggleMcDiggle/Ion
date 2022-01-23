package net.horizonsend.ion.server.powerarmor

import net.horizonsend.ion.server.customitems.CustomItems
import net.horizonsend.ion.server.customitems.types.GenericCustomItem
import net.horizonsend.ion.server.powerarmor.modules.PotionEffectModule
import net.horizonsend.ion.server.powerarmor.modules.RocketModule
import net.horizonsend.ion.server.powerarmor.modules.SpeedModule
import net.kyori.adventure.text.Component
import net.starlegacy.util.Tasks
import org.bukkit.Material.FLINT_AND_STEEL
import org.bukkit.Material.GLASS_PANE
import org.bukkit.potion.PotionEffectType.NIGHT_VISION


object PowerModuleItems {
	private fun registerModuleItem(type: String, typeName: String, weight: Int,  model: Int, craft: String): GenericCustomItem {
		val item = GenericCustomItem(
			id = "power_module_$type",
			displayName = "$typeName Module",
			lore = mutableListOf(Component.text("Weight: $weight")),
			material = FLINT_AND_STEEL,
			model = model,
		)
		CustomItems.register(item)
		Tasks.syncDelay(1) {
			CustomItems.registerShapedRecipe(
				item.id, item.getItem(), "aga", "g*g", "aga", ingredients = mapOf(
					'a' to CustomItems.recipeChoice(CustomItems["aluminum"]!!),
					'g' to CustomItems.recipeChoice(GLASS_PANE),
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
				registerModuleItem("rocket_boosting", "Rocket Boosting", 3, 3, "firework_rocket")
			)
		)
		powerArmorModules.add(
			SpeedModule(
				3,
				registerModuleItem("speed_boosting", "Speed Boosting", 3,2, "feather"),
				effectMultiplier = 1,
				effectDuration = 2,
				power = 1,
			)
		)
		powerArmorModules.add(
			PotionEffectModule(
				1,
				registerModuleItem("night_vision", "Night Vision", 1, 4, "spider_eye"),
				NIGHT_VISION,
				0,
				300,
				0
			)
		)
	}
}
