package net.horizonsend.ion.server.customitems.types

import org.bukkit.Material

class GasItem(
	// This only exists to separate gas canisters from other items. They have no extra functionality.
	// Basically identical to GenericCustomItem
	// Mostly doing this to match the old SL systen.
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
) : CustomItem()