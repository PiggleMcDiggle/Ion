package net.starlegacy.command.misc

import co.aikar.commands.annotation.CommandAlias
import net.starlegacy.command.SLCommand
import net.starlegacy.feature.customitem.powerarmor.ModuleScreen
import org.bukkit.entity.Player

object PowerArmorCommand : SLCommand() {
	@CommandAlias("powerarmor")
	fun onExecute(sender: Player) {
		ModuleScreen(sender)
	}
}
