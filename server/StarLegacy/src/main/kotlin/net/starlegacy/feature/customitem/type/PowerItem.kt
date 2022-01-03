package net.starlegacy.feature.customitem.type

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import net.starlegacy.StarLegacy.Companion.PLUGIN
import net.starlegacy.feature.customitem.CustomItems
import net.starlegacy.util.colorize
import net.starlegacy.util.updateMeta
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType


abstract class PowerItem : CustomItem() {
	abstract val maxPower: Int
	override fun getItem(amount: Int): ItemStack {
		val item = super.getItem(amount)
		item.power = 0
		return item
	}
}

class NotPowerableException(message: String? = null) : Exception(message)
val ITEM_POWER_PREFIX = "&8Power: &7".colorize()
val ItemStack.isPowerableCustomItem: Boolean get() = CustomItems.getCustomItem(this) is PowerItem
val ItemStack.powerableCustomItem: PowerItem? get(){
	if (!this.isPowerableCustomItem) throw NotPowerableException()
	return CustomItems[this] as PowerItem
}
val ItemStack.maxPower: Int? get(){
	if (!this.isPowerableCustomItem) throw NotPowerableException()
	return this.powerableCustomItem!!.maxPower
}


var ItemStack.power: Int
	get() {
		if (!this.isPowerableCustomItem) throw NotPowerableException()
		return this.itemMeta.persistentDataContainer.get(
			NamespacedKey(PLUGIN, "item-power"),
			PersistentDataType.INTEGER
		) ?: 0
	}
	set(value) {
		if (!this.isPowerableCustomItem) throw NotPowerableException()
		val newPower = max(min(value, this.maxPower!!), 0)
		val lore: MutableList<String> = this.lore ?: mutableListOf()
		val text = "$ITEM_POWER_PREFIX$newPower"
		if (lore.size == 0) lore.add(text)
		else lore[0] = text
		this.lore = lore
		this.updateMeta {
			it.persistentDataContainer.set(
				NamespacedKey(PLUGIN, "item-power"),
				PersistentDataType.INTEGER,
				newPower
			)
			// In order to update the durability bar we need to set it to *not* be unbreakable
			it.isUnbreakable = false
			(it as Damageable).damage =
				(this.type.maxDurability - newPower.toFloat() / this.powerableCustomItem!!.maxPower * this.type.maxDurability).roundToInt()
		}
	}
