package net.starlegacy.feature.customitem.type

import org.bukkit.Material

class EnergySwordItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
): CustomItem() {}