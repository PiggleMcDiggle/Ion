package net.starlegacy.feature.customitem.powerarmor.modules

import net.starlegacy.feature.customitem.type.CustomItem
import org.bukkit.entity.Player

abstract class PowerArmorModule {
	abstract val weight: Int
	abstract val customItem: CustomItem

	open fun tickModule(player: Player) {}
	open fun disableModule(player: Player) {}
}