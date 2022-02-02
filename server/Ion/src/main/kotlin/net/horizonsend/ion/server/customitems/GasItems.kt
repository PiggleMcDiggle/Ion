package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.registerShapedRecipe
import net.horizonsend.ion.server.customitems.types.GasItem
import net.starlegacy.util.Tasks
import org.bukkit.Material.SNOWBALL

object GasItems {

	private fun registerGas(id: String, name: String, model: Int): GasItem {
		val item = GasItem(
			id = "gas_canister_${id}",
			displayName = name,
			material = SNOWBALL,
			model = model
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		val empty = registerGas("empty", "<white>Empty", model = 1)
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
		registerGas("helium","<yellow>Helium", model = 2)
		registerGas("oxygen", "<aqua>Oxygen", model = 3)
		registerGas("hydrogen","<green>Hydrogen", model = 4)
		registerGas("nitrogen", "<dark_purple>Nitrogen", model = 5)
		registerGas("carbon_dioxide","<red>Carbon Dioxide", model = 6)
	}
}