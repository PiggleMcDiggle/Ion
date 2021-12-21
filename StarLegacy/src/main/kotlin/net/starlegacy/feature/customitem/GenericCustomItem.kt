package net.starlegacy.feature.customitem

import org.bukkit.Material

class GenericCustomItem(
	// Represents a generic custom item with no frills
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material

	): CustomItem() {
		init {
			CustomItemManager.register(this)
		}
}