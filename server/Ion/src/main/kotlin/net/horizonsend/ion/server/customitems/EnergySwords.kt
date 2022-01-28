package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.itemStackFromId
import net.horizonsend.ion.server.customitems.CustomItems.Companion.recipeChoice
import net.horizonsend.ion.server.customitems.CustomItems.Companion.registerShapedRecipe
import net.horizonsend.ion.server.customitems.types.EnergySwordItem
import net.starlegacy.StarLegacy.Companion.PLUGIN
import net.starlegacy.util.Tasks
import org.bukkit.Bukkit
import org.bukkit.Material.SHIELD

object EnergySwords {

	@Suppress("DEPRECATION") // I want to use .capitalize() lol
	private fun registerEnergySword(name: String, displayColor: String, model: Int, craft: String): EnergySwordItem {
		val item = EnergySwordItem(
			id = "energy_sword_${name.lowercase().replace(" ", "_")}",
			displayName = "<red>Energy Sword <gray>- <$displayColor>${name.capitalize()}",
			material = SHIELD,
			model = model
		)
		CustomItems.register(item)
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				item.id, item.getItem(), "aga", "a*a", "ata", ingredients = mapOf(
					'a' to recipeChoice(itemStackFromId("aluminum")!!),
					'g' to recipeChoice(itemStackFromId("glass_pane")!!),
					'*' to recipeChoice(itemStackFromId(craft)!!),
					't' to recipeChoice(itemStackFromId("titanium")!!)
				)
			)
		}
		return item
	}

	fun register() {
		registerEnergySword("blue", "blue",1, "diamond")
		registerEnergySword("red", "red",2, "redstone")
		registerEnergySword("yellow", "yellow",3, "coal")
		registerEnergySword("green", "green",4, "emerald")
		registerEnergySword("purple", "dark_purple",5, "chetherite")
		registerEnergySword("orange", "gold", 6, "copper_ingot")
		// Energy sword idle sound
		// Use async task and while loop with thread sleep so when it lags it doesnt sound weird
		// The timing of the sounds is very important
		Tasks.async {
			while (PLUGIN.isEnabled) {
				Tasks.sync {
					for (player in Bukkit.getOnlinePlayers()) {
						val main = player.inventory.itemInMainHand
						val offhand = player.inventory.itemInOffHand

						val mainCustomItem = CustomItems[main]
						val offhandCustomItem = CustomItems[offhand]

						if (mainCustomItem != null && mainCustomItem.id.contains("sword")
							|| offhandCustomItem != null && offhandCustomItem.id.contains("sword")
						) {
							player.world.playSound(player.location, "energy_sword.idle", 5.0f, 1.0f)
						}
					}
				}
				try {
					Thread.sleep(2000)
				} catch (e: InterruptedException) {
					e.printStackTrace()
				}
			}
		}
	}
}