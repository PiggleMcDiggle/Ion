package net.starlegacy.feature.customitem.type

import net.horizonsend.ion.Ion.Companion.plugin
import net.kyori.adventure.text.Component
import net.starlegacy.util.updateMeta
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class CustomItem() {
	abstract val id: String
	abstract val model: Int
	abstract val displayName: String
	abstract val material: Material
	open val lore = mutableListOf<Component>()
	open val unbreakable = true

	/**
	 * Returns [amount] of this item in an ItemStack
	 */
	fun getItem(amount: Int = 1): ItemStack {
		return ItemStack(material, amount).updateMeta {
			it.setCustomModelData(model)
			it.isUnbreakable = unbreakable
			it.persistentDataContainer.set(NamespacedKey(plugin, "custom-item-id"), PersistentDataType.STRING, id)
			it.lore(lore)
			it.displayName(Component.text(displayName))
		}
	}

	/**
	 * Called when the player right clicks while holding the item.
	 *
	 * @param event The event.
	 */
	open fun onRightClick(event: PlayerInteractEvent) {}
	/**
	 * Called when the player left clicks while holding the item.
	 *
	 * @param event The event.
	 */
	open fun onLeftClick(event: PlayerInteractEvent) {}
	/**
	 * Called when the player drops the item.
	 *
	 * @param event The event.
	 */
	open fun onDropped(event: PlayerDropItemEvent) {}
	/**
	 * Called immediately after the item is registered.
	 */
	open fun onItemRegistered() {}
	/**
	 * Called when an entity damages another entity while holding the item.
	 *
	 * @param event The event.
	 */
	open fun onHitEntity(event: EntityDamageByEntityEvent) {}
	/**
	 * Called when an entity takes damage from another entity while holding the item.
	 *
	 * @param event The event.
	 */
	open fun onHitWhileHolding(event: EntityDamageByEntityEvent) {}
	/**
	 * Called when the crafting grid is matched to the item.
	 *
	 * @param event The event.
	 */
	open fun onPrepareCraft(event: PrepareItemCraftEvent) {}
}