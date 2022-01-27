package net.horizonsend.ion.server.powerarmor.modules

import net.horizonsend.ion.server.customitems.types.CustomItem
import org.bukkit.entity.Player

/**
 * Base class for all Power Armor modules.
 */
abstract class PowerArmorModule {
	/**
	 * The "weight" this module takes up.
	 */
	abstract val weight: Int

	/**
	 * The custom item that represents this module.
	 */
	abstract val customItem: CustomItem

	/**
	 * Called every module tick. Note this is not necessarily every tick.
	 */
	open fun tickModule(player: Player) {}

	/**
	 * Called when the module is disabled.
	 */
	open fun disableModule(player: Player) {}
}