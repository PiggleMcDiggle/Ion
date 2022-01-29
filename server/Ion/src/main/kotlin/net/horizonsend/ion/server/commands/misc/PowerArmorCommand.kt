package net.horizonsend.ion.server.commands.misc

import co.aikar.commands.annotation.CommandAlias
import net.horizonsend.ion.server.powerarmor.ModuleScreen
import net.starlegacy.command.SLCommand
import org.bukkit.entity.Player

object PowerArmorCommand : SLCommand() {
	@CommandAlias("powerarmor")
	fun onExecute(sender: Player) {
		ModuleScreen(sender)
	}
}
