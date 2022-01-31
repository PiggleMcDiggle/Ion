package net.horizonsend.ion.server.customitems.types

import net.starlegacy.util.Tasks
import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * Implements functionality for a Power Drill
 */
class DrillItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	override val maxPower: Int
) : PowerItem() {

	override fun onLeftClick(event: PlayerInteractEvent) {
		if (event.action != Action.LEFT_CLICK_BLOCK) return
		val block = event.clickedBlock ?: return
		val blockType = block.type
		if (blockType == Material.BEDROCK || blockType == Material.BARRIER) return
		if (!BlockBreakEvent(block, event.player).callEvent()) return

		Tasks.syncDelay(4) { // Figure out why this delay exists?
			if (blockType != block.type) return@syncDelay
			if (event.item!!.power < 20) {
				event.player.sendMessage(ChatColor.RED.toString() + "Out of power.")
				return@syncDelay
			}
			event.item!!.power -= 10
			event.player.world.playSound(event.player.location, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.1f, 1.5f)
			block.world.playEffect(block.location, Effect.STEP_SOUND, blockType)
			block.breakNaturally(ItemStack(Material.DIAMOND_PICKAXE))
		}
		return
	}
}