package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.CustomItems.Companion.recipeChoice
import net.starlegacy.feature.customitem.CustomItems.Companion.registerShapedRecipe
import net.starlegacy.feature.customitem.type.GasItem
import net.starlegacy.util.Tasks
import net.starlegacy.util.stripColor
import org.bukkit.ChatColor
import org.bukkit.Material

object GasItems {

	private fun registerGas(name: String, model: Int): GasItem {
		val item = GasItem(
			id = "gas_canister_${name.stripColor().lowercase().replace(" ", "_")}",
			displayName = name,
			material = Material.SNOWBALL,
			model = model
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		val empty = registerGas("${ChatColor.WHITE}Empty", model = 1)
		Tasks.syncDelay(1){
			registerShapedRecipe(empty.id, empty.getItem(), " i ", "igi", " i ", ingredients = mapOf(
				'i' to recipeChoice(CustomItems["titanium"]!!),
				'g' to recipeChoice(Material.GLASS_PANE)
			))
		}
		registerGas("${ChatColor.YELLOW}Helium", model = 2)
		registerGas("${ChatColor.AQUA}Oxygen", model = 3)
		registerGas("${ChatColor.GREEN}Hydrogen", model = 4)
		registerGas("${ChatColor.DARK_PURPLE}Nitrogen", model = 5)
		registerGas("${ChatColor.RED}Carbon Dioxide", model = 6)
	}

}