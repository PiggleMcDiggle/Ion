package net.starlegacy.feature.customitem.powerarmor

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import java.time.Instant
import java.util.HashMap
import java.util.UUID
import net.starlegacy.cache.nations.NationCache
import net.starlegacy.cache.nations.PlayerCache
import net.starlegacy.feature.customitem.CustomItems
import net.starlegacy.feature.customitem.CustomItems.Companion.itemStackFromId
import net.starlegacy.feature.customitem.CustomItems.Companion.recipeChoice
import net.starlegacy.feature.customitem.CustomItems.Companion.registerShapedRecipe
import net.starlegacy.feature.customitem.getPower
import net.starlegacy.feature.customitem.type.PowerArmorItem
import net.starlegacy.feature.customitem.type.PowerModuleItem
import net.starlegacy.feature.customitem.removePower
import net.starlegacy.listener.SLEventListener
import net.starlegacy.util.Tasks
import net.starlegacy.util.action
import org.bukkit.Color
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
		val helmet = registerPowerArmor("Helmet", 1, 50000, Material.LEATHER_HELMET)
		val chestplate = registerPowerArmor("Chestplate", 1, 50000, Material.LEATHER_CHESTPLATE)
		val leggings = registerPowerArmor("Leggings", 1, 50000, Material.LEATHER_LEGGINGS)
		val boots = registerPowerArmor("Boots", 1, 50000, Material.LEATHER_BOOTS)
		Tasks.syncDelay(1) {
			val items = mapOf(
				'*' to recipeChoice(CustomItems["titanium"]!!),
				'b' to recipeChoice(CustomItems["battery_g"]!!)
			)
			registerShapedRecipe(helmet.id, helmet.getItem(), "*b*", "* *", ingredients = items)
			registerShapedRecipe(chestplate.id, chestplate.getItem(), "* *", "*b*", "***", ingredients = items)
			registerShapedRecipe(leggings.id, leggings.getItem(), "*b*", "* *", "* *", ingredients = items)
			registerShapedRecipe(boots.id, boots.getItem(), "* *", "*b*", ingredients = items)
		}
	}
}

object PowerModuleItems {
	private fun registerModuleItem(type: String, typeName: String, model: Int, craft: String): PowerModuleItem {
		val item = PowerModuleItem(
			id = "power_module_$type",
			displayName = "$typeName Module",
			material = Material.FLINT_AND_STEEL,
			model = model
		)
		CustomItems.register(item)
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				item.id, item.getItem(), "aga", "g*g", "aga", ingredients = mapOf(
					'a' to recipeChoice(CustomItems["aluminum"]!!),
					'g' to recipeChoice(Material.GLASS_PANE),
					'*' to recipeChoice(itemStackFromId(craft)!!)
				)
			)
		}

		return item
	}

	fun register() {
		registerModuleItem("shock_absorbing", "Shock Absorbing", 1, "titanium")
		registerModuleItem("speed_boosting", "Speed Boosting", 2, "feather")
		registerModuleItem("rocket_boosting", "Rocket Boosting", 3, "firework_rocket")
		registerModuleItem("night_vision", "Night Vision", 4, "spider_eye")
		registerModuleItem("environment", "Environment", 5, "chainmail_helmet")
		registerModuleItem("pressure_field", "Pressure Field", 6, "gas_canister_oxygen")
	}
}