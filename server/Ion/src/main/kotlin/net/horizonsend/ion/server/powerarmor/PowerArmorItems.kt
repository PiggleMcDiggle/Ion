package net.horizonsend.ion.server.powerarmor

import net.horizonsend.ion.server.customitems.CustomItems
import net.horizonsend.ion.server.customitems.types.PowerArmorItem
import net.starlegacy.util.Tasks
import org.bukkit.Material
import org.bukkit.Material.LEATHER_BOOTS
import org.bukkit.Material.LEATHER_CHESTPLATE
import org.bukkit.Material.LEATHER_HELMET
import org.bukkit.Material.LEATHER_LEGGINGS

object PowerArmorItems {
	private fun registerPowerArmor(piece: String, model: Int, mat: Material, maxPower: Int): PowerArmorItem {
		val item = PowerArmorItem(
			id = "power_armor_${piece.lowercase().replace(" ", "_")}",
			displayName = "<gold>Power $piece",
			material = mat,
			model = model,
			maxPower = maxPower
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		val helmet = registerPowerArmor("Helmet", 1, LEATHER_HELMET, 50000)
		val chestplate = registerPowerArmor("Chestplate", 1, LEATHER_CHESTPLATE, 50000)
		val leggings = registerPowerArmor("Leggings", 1, LEATHER_LEGGINGS, 50000)
		val boots = registerPowerArmor("Boots", 1, LEATHER_BOOTS, 50000)
		Tasks.syncDelay(1) {
			val items = mapOf(
				'*' to CustomItems.recipeChoice(CustomItems["titanium"]!!),
				'b' to CustomItems.recipeChoice(CustomItems["battery_g"]!!)
			)
			CustomItems.registerShapedRecipe(helmet.id, helmet.getItem(), "*b*", "* *", ingredients = items)
			CustomItems.registerShapedRecipe(
				chestplate.id,
				chestplate.getItem(),
				"* *",
				"*b*",
				"***",
				ingredients = items
			)
			CustomItems.registerShapedRecipe(leggings.id, leggings.getItem(), "*b*", "* *", "* *", ingredients = items)
			CustomItems.registerShapedRecipe(boots.id, boots.getItem(), "* *", "*b*", ingredients = items)
		}
	}
}