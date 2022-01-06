package net.starlegacy.feature.customitem.type

import net.starlegacy.feature.customitem.powerarmor.modules.PowerArmorModule
import org.bukkit.Material

class PowerModuleItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	val module: PowerArmorModule
) : CustomItem()