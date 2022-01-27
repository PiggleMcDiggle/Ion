package net.horizonsend.ion.server.customitems.types

import org.bukkit.Material

/**
 * The item state of a mineral with an ore state, block state, and an item state.
 */
class MineralItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	/**
	 * The ore this item should be smelted from (think iron ore -> iron ingot)
	 */
	val ore: CustomBlockItem,
	/**
	 * The block this item can be crafted into (think iron -> iron block)
	 */
	val block: CustomBlockItem
) : CustomItem()