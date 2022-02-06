package net.horizonsend.ion.server.commands.misc

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import net.horizonsend.ion.server.powerarmor.ModuleScreen
import org.bukkit.entity.Player

object PowerArmorCommand : BaseCommand() {
	@CommandAlias("powerarmor")
	fun onExecute(sender: Player) {
		ModuleScreen(sender)
	}
}
