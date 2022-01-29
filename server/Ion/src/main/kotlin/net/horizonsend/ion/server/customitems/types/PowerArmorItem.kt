package net.horizonsend.ion.server.customitems.types

import org.bukkit.Material

/**
 * Adds no additional functionality over PowerItem,
 * exists to distinguish power armor from other items.
 *
 * Represents a piece of Power Armor
 */
class PowerArmorItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	override val maxPower: Int,
) : PowerItem()