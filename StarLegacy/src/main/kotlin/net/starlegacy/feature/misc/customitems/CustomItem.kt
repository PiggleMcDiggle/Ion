package net.starlegacy.feature.misc.customitems

import net.horizonsend.ion.Ion.Companion.plugin
import net.kyori.adventure.text.Component
import net.starlegacy.util.updateMeta
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class CustomItem {
	abstract val displayName: String
	abstract val id: String
	abstract val lore: MutableList<Component>
	abstract val unbreakable: Boolean
	abstract val model: Int
	abstract val material: Material

	init {
		CustomItemManager.register(this)
	}

	fun getItem(amount: Int = 1) : ItemStack {
		return ItemStack(material, amount).updateMeta {
			it.setCustomModelData(model)
			it.isUnbreakable = unbreakable
			it.persistentDataContainer.set(NamespacedKey(plugin, "custom-item-id"), PersistentDataType.STRING, id)
			it.lore(lore)
		}
	}

	open fun onRightClick(event: PlayerInteractEvent) {}
	open fun onLeftClick(event: PlayerInteractEvent) {}
	open fun onDropped() {}
	open fun onPickup() {}
}