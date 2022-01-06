package net.starlegacy.feature.customitem.type.module

import net.starlegacy.feature.customitem.type.CustomItem
import org.bukkit.Material

class PowerModuleItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
) : CustomItem()