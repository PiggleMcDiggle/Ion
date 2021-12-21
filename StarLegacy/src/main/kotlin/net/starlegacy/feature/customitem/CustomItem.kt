package net.starlegacy.feature.customitem

import net.horizonsend.ion.Ion.Companion.plugin
import net.kyori.adventure.text.Component
import net.starlegacy.util.updateMeta
import org.bukkit.Material
import org.bukkit.NamespacedKey
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

	fun getItem(amount: Int = 1): ItemStack {
		return ItemStack(material, amount).updateMeta {
			it.setCustomModelData(model)
			it.isUnbreakable = unbreakable
			it.persistentDataContainer.set(NamespacedKey(plugin, "custom-item-id"), PersistentDataType.STRING, id)
			it.lore(lore)
		}
	}

	open fun onRightClick(event: PlayerInteractEvent) {}
	open fun onLeftClick(event: PlayerInteractEvent) {}
}