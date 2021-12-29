package net.starlegacy.listener.misc

import net.starlegacy.feature.chat.ChannelSelections
import net.starlegacy.feature.chat.ChatChannel
import net.starlegacy.listener.SLEventListener
import net.starlegacy.util.SLTextStyle
import net.starlegacy.util.colorize
import net.starlegacy.util.vaultChat
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatListener : SLEventListener() {
	override fun supportsVanilla(): Boolean {
		return true
	}

	@EventHandler(priority = EventPriority.LOWEST)
	fun onChat(event: AsyncPlayerChatEvent) {
		val prefix = vaultChat.getPlayerPrefix(event.player)
		val suffix = vaultChat.getPlayerSuffix(event.player)
		event.format = "$prefix%s$suffix ${SLTextStyle.DARK_GRAY}» ${SLTextStyle.RESET}%s".colorize()

		if (!event.message.startsWith("!")) {
			val channel = ChannelSelections[event.player]
			event.message = "${channel.messageColor}${event.message}"
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	fun onChat2(event: AsyncPlayerChatEvent) {
		if (!event.isCancelled) {
			event.isCancelled = true

			val channel = when {
				event.message.startsWith("!") -> ChatChannel.GLOBAL
				else -> ChannelSelections[event.player]
			}
			event.message = event.message.removePrefix("!").trim()
			if (event.message.isBlank()) {
				return
			}
			channel.onChat(event.player, event)
		}
	}
}
