package net.starlegacy.feature.customitem

import net.horizonsend.ion.Ion.Companion.plugin
import net.starlegacy.SLComponent
import net.starlegacy.feature.customitem.type.CustomItem
import net.starlegacy.feature.customitem.type.GenericCustomItem
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class CustomItemManager: Listener {
	companion object {
		val customItems = mutableMapOf<String, CustomItem>()

		fun register(item: CustomItem) {
			// Check for duplicate custom model data
			customItems.forEach{ (id, customItem) ->
				if (customItem.model == item.model && customItem.material == item.material) {
					plugin.logger.warning("Multiple custom items have registered for the same material and model data!")
					plugin.logger.warning("${customItem.id} and ${item.id} are both using ${customItem.material.name} and ${customItem.model}")
				}
			}
			if (customItems.put(item.id, item) != null) {
				plugin.logger.warning("Multiple custom items with id ${item.id} have been registered!")
			}
		}
		fun getCustomItem(stack: ItemStack): CustomItem? = customItems[stack.itemMeta.persistentDataContainer.get(NamespacedKey(plugin, "custom-item-id"), PersistentDataType.STRING)]
	}

	init {
		plugin.server.pluginManager.registerEvents(this, plugin)
		registerItems()
	}

	@EventHandler
	fun onInteract(event: PlayerInteractEvent) {
		if (event.item == null) return
		val item = customItems[
				event.item!!.itemMeta.persistentDataContainer.get(
					NamespacedKey(plugin, "custom-item-id"), PersistentDataType.STRING
				) ?: return]
			?: return

		when (event.action) {
			Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR -> {
				 item.onLeftClick(event)
			}
			Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
				item.onRightClick(event)
			}
			else -> return // ugh
		}
	}
	@EventHandler
	fun onDrop(event: PlayerDropItemEvent) {
		val item = customItems[
				event.itemDrop.itemStack.itemMeta.persistentDataContainer.get(
					NamespacedKey(plugin, "custom-item-id"), PersistentDataType.STRING
				) ?: return]
			?: return

		item.onDropped(event)
	}

	private fun registerItems() {
		// Register items here
		PlanetIcons.register()
		MiscItems.register()
		GasItems.register()
	}
}