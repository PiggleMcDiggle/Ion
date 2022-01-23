package net.horizonsend.ion.server.customitems.types

import net.kyori.adventure.text.Component
import org.bukkit.Material

class GenericCustomItem(
	// Represents a generic custom item with no frills
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	override val lore: MutableList<Component> = mutableListOf()
) : CustomItem()