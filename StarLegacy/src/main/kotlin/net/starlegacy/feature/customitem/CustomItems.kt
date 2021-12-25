package net.starlegacy.feature.customitem

import net.horizonsend.ion.Ion.Companion.plugin
import net.starlegacy.feature.customitem.type.CustomItem
import net.starlegacy.feature.customitem.type.GenericCustomItem
import net.starlegacy.feature.customitem.type.PowerItem
import net.starlegacy.util.colorize
import net.starlegacy.util.updateMeta
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.max
import kotlin.math.min

class CustomItems: Listener {
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
			item.onItemRegistered()
			plugin.logger.warning("Registered custom item ${item.id}")
		}
		fun all(): Collection<CustomItem> = customItems.values
		fun getCustomItem(id: String?): CustomItem? = customItems[id]
		fun getCustomItem(stack: ItemStack?): CustomItem? = customItems[stack?.itemMeta?.persistentDataContainer?.get(NamespacedKey(plugin, "custom-item-id"), PersistentDataType.STRING)]
		operator fun get(id: String?): CustomItem? = getCustomItem(id)
		operator fun get(item: ItemStack?): CustomItem? = getCustomItem(item)
		val blankItem = GenericCustomItem("blank_item", 0, "Blank Custom Item", Material.EMERALD)
	}

	init {
		plugin.server.pluginManager.registerEvents(this, plugin)
		registerItems()
	}

	private fun registerItems() {
		// Register items here
		PlanetIcons.register()
		MiscItems.register()
		GasItems.register()
		BatteryItems.register()
		EnergySwords.register()
		BlasterItems.register()
		PowerArmorItems.register()
		PowerModuleItems.register()
	}

	@EventHandler
	fun onInteract(event: PlayerInteractEvent) {
		val item = getCustomItem(event.item) ?: return
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
		val item = getCustomItem(event.itemDrop.itemStack) ?: return
		item.onDropped(event)
	}
	@EventHandler
	fun onCraft(event: PrepareItemCraftEvent) {
		val item = getCustomItem(event.inventory.result) ?: return
		item.onPrepareCraft(event)
	}
	@EventHandler
	fun onHit(event: EntityDamageByEntityEvent) {
		val damager = event.damager as? LivingEntity ?: return
		val itemInHand = damager.equipment?.itemInMainHand ?: return
		getCustomItem(itemInHand)?.onHitEntity(event) ?: return
	}
	@EventHandler
	fun onHitWhileHolding(event: EntityDamageByEntityEvent) {
		val damaged = event.entity as? LivingEntity ?: return
		val itemInHand = damaged.equipment?.itemInMainHand ?: return
		getCustomItem(itemInHand)?.onHitWhileHolding(event) ?: return
	}
}


// region power
// Funcions for dealing with powerable items

val ITEM_POWER_PREFIX = "&8Power: &7".colorize()

fun isPowerable(itemStack: ItemStack): Boolean {
	return CustomItems.getCustomItem(itemStack) is PowerItem
}

/**
 * Get the power of the item
 * @return The item's power if it is powerable, otherwise -1
 */
fun getPower(itemStack: ItemStack): Int {
	if (!isPowerable(itemStack)) {
		return -1
	}
	return itemStack.itemMeta.persistentDataContainer.get(
		NamespacedKey(plugin, "item-power"),
		PersistentDataType.INTEGER
	)
		?: 0
}

/**
 * Get the maximum amount of power an item can hold
 * @return The item's max power if it is powerable, otherwise -1
 */
fun getMaxPower(itemStack: ItemStack): Int {
	val poweredCustomItem = CustomItems.getCustomItem(itemStack) as? PowerItem ?: return -1
	return poweredCustomItem.maxPower
}

/**
 * Set the power of the item to the new power if it is powerable
 * Automatically limits to max power
 * @return The old power if it was a powerable item, otherwise -1
 */
fun setPower(itemStack: ItemStack, power: Int): Int {
	val poweredCustomItem = CustomItems.getCustomItem(itemStack) as? PowerItem ?: return -1

	val oldPower = getPower(itemStack)
	val newPower = max(min(power, poweredCustomItem.maxPower), 0)

	val lore: MutableList<String> = itemStack.lore ?: mutableListOf()
	val text = "$ITEM_POWER_PREFIX$newPower"
	if (lore.size == 0) lore.add(text)
	else lore[0] = text
	itemStack.lore = lore

	itemStack.updateMeta {
		it.persistentDataContainer.set(
			NamespacedKey(plugin, "item-power"),
			PersistentDataType.INTEGER,
			newPower
		)
	}
	return oldPower
}

/**
 * Adds the given amount of power to the item if it is powerable
 * Automatically limits to max power
 * @return The old power if it was powerable, otherwise -1
 */
fun addPower(itemStack: ItemStack, amount: Int): Int {
	val power = getPower(itemStack)
	if (power == -1) return -1

	return setPower(itemStack, power + amount)
}

/**
 * removes the given amount of power to the item if it is powerable
 * Automatically limits to max power
 * @return The old power if it was powerable, otherwise -1
 */
fun removePower(itemStack: ItemStack, amount: Int): Int {
	val power = getPower(itemStack)
	if (power == -1) return -1

	return setPower(itemStack, power - amount)
}
// endregion