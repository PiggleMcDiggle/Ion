package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.recipeChoice
import net.horizonsend.ion.server.customitems.CustomItems.Companion.registerShapedRecipe
import net.horizonsend.ion.server.customitems.types.BatteryItem
import net.starlegacy.util.Tasks
import org.bukkit.Material
import org.bukkit.Material.GLOWSTONE_DUST
import org.bukkit.Material.REDSTONE
import org.bukkit.Material.SEA_LANTERN
import org.bukkit.Material.SNOWBALL

object BatteryItems {

	private fun registerBattery(id: String, name: String, model: Int, power: Int, craft: Material): BatteryItem {
		val item = BatteryItem(
			id = id,
			displayName = name,
			material = SNOWBALL,
			model = model,
			maxPower = power,
		)
		CustomItems.register(item)
		// Delay one tick so other items can register first, before trying to use them for crafting
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				item.id, item.getItem(), "aba", "aba", "aba", ingredients = mapOf(
					'a' to recipeChoice(CustomItems["aluminum"]!!),
					'b' to recipeChoice(craft)
				)
			)
		}
		return item
	}

	fun register() {
		registerBattery("battery_a", "Battery <red>A", 7, 500, GLOWSTONE_DUST)
		registerBattery("battery_m", "Battery <green>M", 8, 1000, REDSTONE)
		registerBattery("battery_g", "Battery <gold>G", 9, 2000, SEA_LANTERN)
	}
}