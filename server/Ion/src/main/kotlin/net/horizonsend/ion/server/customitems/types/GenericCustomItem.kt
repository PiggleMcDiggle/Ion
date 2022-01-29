package net.horizonsend.ion.server.customitems.types

import org.bukkit.Material

/**
 * Adds no additional functionality over [CustomItem]
 * Exists for simple custom items that are basically just named items
 */
class GenericCustomItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	override val lore: MutableList<String> = mutableListOf()
) : CustomItem()