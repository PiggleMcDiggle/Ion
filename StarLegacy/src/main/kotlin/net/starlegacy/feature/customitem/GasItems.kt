package net.starlegacy.feature.customitem

import net.starlegacy.feature.customitem.type.CustomItem
import net.starlegacy.feature.customitem.type.GasItem
import net.starlegacy.util.stripColor
import org.bukkit.ChatColor
import org.bukkit.Material

class GasItems {
	companion object {
		private fun registerGas(name: String, model: Int): GasItem {
			val item = GasItem(
				id = "gas_canister_${name.stripColor().lowercase().replace(" ", "_")}",
				displayName = name,
				material = Material.APPLE,
				model = model
			)
			CustomItemManager.register(item)
			return item
		}

		fun register() {
			registerGas("${ChatColor.WHITE}Empty", model = 1)
			registerGas("${ChatColor.YELLOW}Helium", model = 2)
			registerGas("${ChatColor.AQUA}Oxygen", model = 3)
			registerGas("${ChatColor.GREEN}Hydrogen", model = 4)
			registerGas("${ChatColor.DARK_PURPLE}Nitrogen", model = 5)
			registerGas("${ChatColor.RED}Carbon Dioxide", model = 6)
		}
	}
}