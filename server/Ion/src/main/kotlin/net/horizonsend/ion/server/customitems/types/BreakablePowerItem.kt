package net.horizonsend.ion.server.customitems.types

import net.horizonsend.ion.server.customitems.CustomItems
import net.starlegacy.StarLegacy.Companion.PLUGIN
import net.starlegacy.util.colorize
import net.starlegacy.util.updateMeta
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.max
import kotlin.math.min

abstract class BreakablePowerItem : PowerItem() {
	abstract val maxUses: Int
	override fun getItem(amount: Int): ItemStack {
		val item = super.getItem(amount)
		item.uses = maxUses
		return item
	}
}

val ITEM_USES_PREFIX = "&8Uses: &7".colorize()

class NotBreakablePowerableException(message: String? = null) : Exception(message)

val ItemStack.isBreakablePowerableCustomItem: Boolean get() = CustomItems.getCustomItem(this) is BreakablePowerItem
val ItemStack.breakablePowerableCustomItem: BreakablePowerItem? get() = CustomItems[this] as? BreakablePowerItem

var ItemStack.uses: Int
	get() {
		if (!this.isBreakablePowerableCustomItem) throw NotBreakablePowerableException()
		return this.itemMeta.persistentDataContainer.get(
			NamespacedKey(PLUGIN, "item-uses"),
			PersistentDataType.INTEGER
		) ?: 0
	}
	set(value) {
		if (!this.isBreakablePowerableCustomItem) throw NotBreakablePowerableException()
		val maxUses = this.breakablePowerableCustomItem!!.maxUses
		var newUses = max(min(value, maxUses), 0)
		if (newUses <= 0) {
			this.amount--
			newUses = maxUses
			return
		}
		val lore: MutableList<String> = this.lore ?: mutableListOf()
		if (lore.size < 2) lore.add("$ITEM_USES_PREFIX$newUses / $maxUses")
		else lore[1] = "$ITEM_USES_PREFIX$newUses / $maxUses"
		this.lore = lore
		this.updateMeta {
			it.persistentDataContainer.set(
				NamespacedKey(PLUGIN, "item-uses"),
				PersistentDataType.INTEGER,
				newUses
			)
		}
	}