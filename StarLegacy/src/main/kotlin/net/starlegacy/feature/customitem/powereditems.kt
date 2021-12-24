package net.starlegacy.feature.customitem

import net.horizonsend.ion.Ion.Companion.plugin
import net.starlegacy.feature.customitem.type.PowerItem
import net.starlegacy.util.colorize
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.max
import kotlin.math.min

val ITEM_POWER_PREFIX = "&8Power: &7".colorize()

fun isPowerable(itemStack: ItemStack): Boolean {
	return CustomItemManager.getCustomItem(itemStack) is PowerItem
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
	val poweredCustomItem = CustomItemManager.getCustomItem(itemStack) as? PowerItem ?: return -1
	return poweredCustomItem.maxPower
}

/**
 * Set the power of the item to the new power if it is powerable
 * Automatically limits to max power
 * @return The old power if it was a powerable item, otherwise -1
 */
fun setPower(itemStack: ItemStack, power: Int): Int {
	val poweredCustomItem = CustomItemManager.getCustomItem(itemStack) as? PowerItem ?: return -1

	val oldPower = getPower(itemStack)
	val newPower = max(min(power, poweredCustomItem.maxPower), 0)

	val lore: MutableList<String> = itemStack.lore ?: mutableListOf()
	val text = "$ITEM_POWER_PREFIX$newPower"
	if (lore.size == 0) lore.add(text)
	else lore[0] = text
	itemStack.lore = lore

	itemStack.itemMeta.persistentDataContainer.set(
		NamespacedKey(plugin, "item-power"),
		PersistentDataType.INTEGER,
		newPower
	)
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
