package net.horizonsend.ion.server.customitems.types

import net.starlegacy.feature.misc.CustomBlock
import net.starlegacy.feature.misc.CustomBlocks
import org.bukkit.Material

/**
 * Links a [CustomItem] to a [CustomBlock]
 */
class CustomBlockItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	/**
	 * The ID of the custom block this item represents
	 */
	val customBlockId: String
) : CustomItem() {

	/**
	 * The [CustomBlock] this item should place when the player attempts to place it
	 */
	val customBlock: CustomBlock
		get() = CustomBlocks[customBlockId] ?: error("Custom block $customBlockId not found for custom item $id")
}