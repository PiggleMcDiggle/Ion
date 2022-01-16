package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.recipeChoice
import net.horizonsend.ion.server.customitems.CustomItems.Companion.registerShapedRecipe
import net.horizonsend.ion.server.customitems.types.GasItem
import net.starlegacy.util.Tasks
import net.starlegacy.util.stripColor
import org.bukkit.ChatColor.*
import org.bukkit.Material.*

object GasItems {

	private fun registerGas(name: String, model: Int): GasItem {
		val item = GasItem(
			id = "gas_canister_${name.stripColor().lowercase().replace(" ", "_")}",
			displayName = name,
			material = SNOWBALL,
			model = model
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		val empty = registerGas("${WHITE}Empty", model = 1)
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				empty.id, empty.getItem(), " i ", "igi", " i ", ingredients = mapOf(
					'i' to recipeChoice(CustomItems["titanium"]!!),
					'g' to recipeChoice(GLASS_PANE)
				)
			)
		}
		registerGas("${YELLOW}Helium", model = 2)
		registerGas("${AQUA}Oxygen", model = 3)
		registerGas("${GREEN}Hydrogen", model = 4)
		registerGas("${DARK_PURPLE}Nitrogen", model = 5)
		registerGas("${RED}Carbon Dioxide", model = 6)
	}
}