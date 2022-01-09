package net.horizonsend.ion.server.customitems.types

import org.bukkit.Material

class BatteryItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	override val maxPower: Int,
) : PowerItem()