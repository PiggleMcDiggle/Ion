package net.starlegacy.command.misc

import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import net.starlegacy.command.SLCommand
import net.starlegacy.feature.customitem.type.isPowerableCustomItem
import net.starlegacy.feature.customitem.type.power
import net.starlegacy.util.displayName
import net.starlegacy.util.green
import net.starlegacy.util.msg
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@CommandAlias("battery")
@CommandPermission("machinery.battery")
object BatteryCommand : SLCommand() {
	private fun getPowerableItemInHand(sender: Player): ItemStack {
		val item = sender.inventory.itemInMainHand

		if (item == null || !item.isPowerableCustomItem) {
			throw ConditionFailedException("You must be holding a powerable item to do this!")
		}

		return item
	}
	@Subcommand("get")
	fun onGet(sender: Player){
		val item = getPowerableItemInHand(sender)
		sender.msg("${item.displayName} currently has ${item.power} power.")
	}

	@Subcommand("set")
	@CommandCompletion("0|10|100|1000|10000")
	fun onSet(sender: Player, amount: Int) {
		val item = getPowerableItemInHand(sender)
		item.power = amount
		sender msg green("Set power of ${item.displayName} to $amount")
	}

	@Subcommand("add")
	fun onAdd(sender: Player, amount: Int) {
		val item = getPowerableItemInHand(sender)
		item.power += amount
		sender msg green("Added $amount power to ${item.displayName}")
	}

	@Subcommand("remove")
	fun onRemove(sender: Player, amount: Int) {
		val item = getPowerableItemInHand(sender)
		item.power -= amount
		sender msg green("Removed $amount power from ${item.displayName}")
	}
}
