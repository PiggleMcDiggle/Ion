package net.horizonsend.ion.server.customitems.types

import net.horizonsend.ion.server.customitems.CustomItems
import net.starlegacy.StarLegacy.Companion.PLUGIN
import net.starlegacy.util.Tasks
import net.starlegacy.util.colorize
import net.starlegacy.util.updateMeta
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


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
val ItemStack.powerableCustomItem: PowerItem? get() = CustomItems[this] as? PowerItem

val ItemStack.maxPower: Int?
	get() {
		if (!this.isPowerableCustomItem) throw NotPowerableException()
		return this.powerableCustomItem!!.maxPower
	}

fun ItemStack.updatePowerDurability() {
	if (!this.isPowerableCustomItem) throw NotPowerableException()
	this.updateMeta {
		// In order to update the durability bar we need to set it to *not* be unbreakable
		it.isUnbreakable = false
		(it as Damageable).damage =
			(this.type.maxDurability - this.power.toFloat() / this.powerableCustomItem!!.maxPower * this.type.maxDurability).roundToInt()
	}
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
		}
		this.updatePowerDurability()
	}

class PowerItemBreakCanceller : Listener {
	// Have to cancel damage on powerable items, otherwise ones at 0 power will break
	@EventHandler
	fun preventDamage(event: PlayerItemBreakEvent) {
		if (!event.brokenItem.isPowerableCustomItem) return
		// https://bukkit.org/threads/playeritembreakevent-cancelling.282678/
		event.brokenItem.amount += 1 // If there's a custom item dupe it's probably because of this
		Tasks.syncDelay(1) { event.brokenItem.updatePowerDurability() }
	}
}

