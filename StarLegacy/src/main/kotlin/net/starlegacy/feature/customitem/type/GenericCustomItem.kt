package net.starlegacy.feature.customitem.type

import net.starlegacy.feature.customitem.CustomItems
import org.bukkit.Material

class GenericCustomItem(
	// Represents a generic custom item with no frills
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material

) : CustomItem() {
	init {
		CustomItems.register(this)
	}
}