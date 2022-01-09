package net.starlegacy.feature.customitem.type

import net.starlegacy.feature.misc.CustomBlock
import net.starlegacy.feature.misc.CustomBlocks
import org.bukkit.Material

class CustomBlockItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	val customBlockId: String
) : CustomItem() {

	val customBlock: CustomBlock
		get() = CustomBlocks[customBlockId] ?: error("Custom block $customBlockId not found for custom item $id")
}