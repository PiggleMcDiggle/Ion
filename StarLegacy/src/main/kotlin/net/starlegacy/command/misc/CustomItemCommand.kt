package net.starlegacy.command.misc

import co.aikar.commands.annotation.*
import net.starlegacy.command.SLCommand
import net.starlegacy.feature.customitem.type.CustomItem
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import net.starlegacy.util.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CustomItemCommand : SLCommand() {
	@CommandAlias("customitem")
	@CommandPermission("machinery.customitem")
	@CommandCompletion("@customitems 1|16|64 @players")
	fun onGive(
		sender: CommandSender,
		customItem: CustomItem,
		@Default("1") amount: Int,
		@Optional target: OnlinePlayer?
	) {
		val player = target?.player ?: sender as? Player ?: fail { "Console must specify a target player" }
		failIf(amount <= 0) { "Amount cannot be <= 0" }

		val item = customItem.itemStack(amount)
		val result = player.inventory.addItem(item)

		if (result.isEmpty()) {
			sender msg green("Gave ") +
					white("${amount}x ${customItem.displayName}") +
					green(" to ${player.name}")
		} else {
			val extra = result.values.sumBy { it.amount }
			sender msg red("Could not fit $extra out of the $amount items in ${player.name}'s inventory!")
		}
	}
}
