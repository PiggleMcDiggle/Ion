package net.starlegacy.feature.customitem

import net.horizonsend.ion.Ion.Companion.plugin
import net.starlegacy.feature.customitem.CustomItems.Companion.itemStackFromId
import net.starlegacy.feature.customitem.CustomItems.Companion.recipeChoice
import net.starlegacy.feature.customitem.CustomItems.Companion.registerShapedRecipe
import net.starlegacy.feature.customitem.type.EnergySwordItem
import net.starlegacy.util.Tasks
import org.bukkit.Bukkit
import org.bukkit.Material

object EnergySwords {

	private fun registerEnergySword(name: String, model: Int, craft: String): EnergySwordItem {
		val item = EnergySwordItem(
			id = "energy_sword_${name.lowercase().replace(" ", "_")}",
			displayName = "Energy Sword - $name",
			material = Material.SHIELD,
			model = model
		)
		CustomItems.register(item)
		Tasks.syncDelay(1){
			registerShapedRecipe(item.id, item.getItem(), "aga", "a*a", "ata", ingredients = mapOf(
				'a' to recipeChoice(itemStackFromId("aluminum")!!),
				'g' to recipeChoice(itemStackFromId("glass_pane")!!),
				'*' to recipeChoice(itemStackFromId(craft)!!),
				't' to recipeChoice(itemStackFromId("titanium")!!)
			))
		}
		return item
	}

	fun register() {
		registerEnergySword("blue", 1, "diamond")
		registerEnergySword("red", 2, "redstone")
		registerEnergySword("yellow", 3, "coal")
		registerEnergySword("green", 4, "emerald")
		registerEnergySword("purple", 5, "chetherite")
		registerEnergySword("orange", 6, "copper")

		// Energy sword idle sound
		// Use async task and while loop with thread sleep so when it lags it doesnt sound weird
		// The timing of the sounds is very important
		Tasks.async {
			while (plugin.isEnabled) {
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