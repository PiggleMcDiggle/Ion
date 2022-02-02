package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.registerShapedRecipe
import net.horizonsend.ion.server.customitems.types.GasItem
import net.starlegacy.util.Tasks
import net.starlegacy.util.stripColor
import org.bukkit.ChatColor.AQUA
import org.bukkit.ChatColor.DARK_PURPLE
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.ChatColor.WHITE
import org.bukkit.ChatColor.YELLOW
import org.bukkit.Material.SNOWBALL

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
				empty.getItem(),
				listOf(
					null,       "titanium",   null,
					"titanium", "glass_pane", "titanium",
					null,       "titanium",   null
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