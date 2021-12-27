package net.starlegacy.feature.customitem

import net.horizonsend.ion.Ion.Companion.plugin
import net.starlegacy.feature.customitem.type.EnergySwordItem
import net.starlegacy.feature.gear.Gear
import net.starlegacy.util.Tasks
import org.bukkit.Bukkit
import org.bukkit.Material

object EnergySwords {

	private fun registerEnergySword(name: String, model: Int): EnergySwordItem {
		val item = EnergySwordItem(
			id = "energy_sword_${name.lowercase().replace(" ", "_")}",
			displayName = "Energy Sword - $name",
			material = Material.SHIELD,
			model = model
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		registerEnergySword("blue", 1)
		registerEnergySword("red", 2)
		registerEnergySword("yellow", 3)
		registerEnergySword("green", 4)
		registerEnergySword("purple", 5)
		registerEnergySword("orange", 6)

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