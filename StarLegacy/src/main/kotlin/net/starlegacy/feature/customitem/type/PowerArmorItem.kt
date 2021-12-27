package net.starlegacy.feature.customitem.type

import org.bukkit.Material

class PowerArmorItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	override val maxPower: Int
) : PowerItem()