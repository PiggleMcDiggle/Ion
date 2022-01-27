package net.horizonsend.ion.server.customitems.types

import org.bukkit.Material

/**
 * Represents a Gas Canister.
 * Only exists to distinguish gas canisters from other items, contains no additional functionality.
 * Exists to match the original StarLegacy system
 */
class GasItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
) : CustomItem()