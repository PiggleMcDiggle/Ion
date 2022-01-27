package net.horizonsend.ion.server.customitems.types

import net.horizonsend.ion.server.toMiniMessage
import net.kyori.adventure.text.Component
import net.starlegacy.StarLegacy.Companion.PLUGIN
import net.starlegacy.util.updateMeta
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Base class for all custom items
 */
abstract class CustomItem {
	/**
	 * ID of the custom item, used to identify it. This must be unique to all custom items
	 */
	abstract val id: String

	/**
	 * CustomModelData integer. Should be aligned with the resource pack
	 */
	abstract val model: Int

	/**
	 * The displayName of the item. Can use MiniMessage formatting
	 */
	abstract val displayName: String

	/**
	 * The base [Material] for the custom item.
	 */
	abstract val material: Material

	/**
	 * The allowable [Enchantment]s for this custom item.
	 * Attempts to enchant this item using an anvil or enchanting table with other enchants will fail.
	 *
	 * Does not necessarily allow enchants, the [material] of this CustomItem needs to allow them too.
	 * For instance, if you try to enchant a sword with Protection, it will fail, even if Protection is part of [allowedEnchants]
	 */
	open val allowedEnchants: MutableSet<Enchantment> = mutableSetOf()

	/**
	 * The lore of the item. MiniMessage formatting can be used.
	 */
	open val lore = mutableListOf<String>()

	/**
	 * Whether this item is unbreakable
	 */
	open val unbreakable = true

	/**
	 * Returns [amount] of this item in an ItemStack
	 */
	open fun getItem(amount: Int = 1): ItemStack {
		val loreComponents = mutableListOf<Component>()
		lore.forEach{loreComponents.add(it.toMiniMessage())}
		return ItemStack(material, amount).updateMeta {
			it.setCustomModelData(model)
			it.isUnbreakable = unbreakable
			it.persistentDataContainer.set(NamespacedKey(PLUGIN, "custom-item-id"), PersistentDataType.STRING, id)
			it.lore(loreComponents)
			it.displayName(displayName.toMiniMessage())
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