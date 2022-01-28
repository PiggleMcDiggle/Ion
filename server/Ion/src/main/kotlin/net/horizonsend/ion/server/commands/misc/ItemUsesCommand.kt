package net.horizonsend.ion.server.commands.misc

import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import net.horizonsend.ion.server.customitems.types.isBreakablePowerableCustomItem
import net.horizonsend.ion.server.customitems.types.uses
import net.horizonsend.ion.server.sendMiniMessage
import net.starlegacy.command.SLCommand
import net.starlegacy.util.displayName
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@CommandAlias("uses")
@CommandPermission("machinery.itemuses")
object ItemUsesCommand : SLCommand() {
	private fun getBreakablePowerableItemInHand(sender: Player): ItemStack {
		val item = sender.inventory.itemInMainHand

		if (item == null || !item.isBreakablePowerableCustomItem) {
			throw ConditionFailedException("You must be holding a breakable powerable item to do this!")
		}

		return item
	}

	@Subcommand("get")
	fun onGet(sender: Player) {
		val item = getBreakablePowerableItemInHand(sender)
		sender.sendMiniMessage("<green>${item.displayName} currently has ${item.uses} uses.")
	}

	@Subcommand("set")
	@CommandCompletion("0|10|100|1000|10000")
	fun onSet(sender: Player, amount: Int) {
		val item = getBreakablePowerableItemInHand(sender)
		item.uses = amount
		sender.sendMiniMessage("<green>Set uses of ${item.displayName} to $amount")
	}

	@Subcommand("add")
	fun onAdd(sender: Player, amount: Int) {
		val item = getBreakablePowerableItemInHand(sender)
		item.uses += amount
		sender.sendMiniMessage("<green>Added $amount uses to ${item.displayName}")
	}

	@Subcommand("remove")
	fun onRemove(sender: Player, amount: Int) {
		val item = getBreakablePowerableItemInHand(sender)
		item.uses -= amount
		sender.sendMiniMessage("<green>Removed $amount uses from ${item.displayName}")
	}
}
