package net.horizonsend.ion.server.powerarmor.modules

import net.horizonsend.ion.server.customitems.types.CustomItem
import org.bukkit.entity.Player

abstract class PowerArmorModule {
	abstract val weight: Int
	abstract val customItem: CustomItem

	open fun tickModule(player: Player) {}
	open fun disableModule(player: Player) {}
}