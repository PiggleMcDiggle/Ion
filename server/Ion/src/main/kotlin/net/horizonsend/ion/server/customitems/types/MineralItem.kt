package net.horizonsend.ion.server.customitems.types

import org.bukkit.Material

class MineralItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	val ore: CustomBlockItem,
	val block: CustomBlockItem
) : CustomItem()