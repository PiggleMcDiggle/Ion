package net.horizonsend.ion.server.powerarmor

import net.horizonsend.ion.server.Screen
import net.horizonsend.ion.server.customitems.types.isPowerableCustomItem
import net.horizonsend.ion.server.customitems.types.power
import net.kyori.adventure.text.Component
import net.starlegacy.util.red
import org.bukkit.Material.BLUE_STAINED_GLASS_PANE
import org.bukkit.Material.GRAY_STAINED_GLASS_PANE
import org.bukkit.Material.GREEN_STAINED_GLASS_PANE
import org.bukkit.Material.LIME_STAINED_GLASS
import org.bukkit.Material.LIME_STAINED_GLASS_PANE
import org.bukkit.Material.ORANGE_STAINED_GLASS_PANE
import org.bukkit.Material.RED_STAINED_GLASS
import org.bukkit.Material.RED_STAINED_GLASS_PANE
import org.bukkit.Material.YELLOW_STAINED_GLASS_PANE
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType.CHEST
import org.bukkit.inventory.ItemStack
import java.time.Instant

class ModuleScreen(player: Player) : Screen() {
	override val playerEditableSlots = setOf(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 26)

	init {
		val secondsToWait =
			PowerArmorListener.guiCombatCooldownSeconds -
					((System.currentTimeMillis() - (PowerArmorListener.playersInCombat[player.uniqueId]?:0)) / 1000)
		if (secondsToWait <= 0) {
			createScreen(player, CHEST, "Power Armor Modules")

			setAll(setOf(5, 6, 7, 14, 15, 16, 17, 23, 24, 25), ItemStack(GRAY_STAINED_GLASS_PANE))

			// Put instances of every module they have in the slots
			val slots = arrayOf(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21)
			var index = 0

			// Clear their modules, they get added back on screen close
			// Don't just set their modules to empty or the modules won't disable
			player.armorModules.forEach {
				screen.setItem(slots[index], it.customItem.getItem())
				player.removeArmorModule(it)
				index++
			}
			// Insert the toggle button, the fuel indicator, and the weight indicators
			updateStatus()
		}
		else {
			player.sendMessage(red("You need to be out of combat for $secondsToWait more seconds before editing power armor!"))
		}
	}

	private fun updateStatus() {
		// Update the colored status bar that tells the player's module weight.
		// Since we temporarily removed all of their modules, we can't use PlayerPowerArmor.moduleWeight
		val slots = setOf(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21)
		var weight = 0
		slots.forEach {
			val module = screen.getItem(it)?.armorModule
			if (module != null) weight += module.weight
		}
		// Figure out what color to make the status bar
		val color = ItemStack(
			if (weight <= maxModuleWeight) {
				RED_STAINED_GLASS_PANE
			} else {
				LIME_STAINED_GLASS_PANE
			}
		)
		// Name it
		val colorMeta = color.itemMeta
		colorMeta.displayName(Component.text("Weight: $weight / $maxModuleWeight"))
		color.itemMeta = colorMeta
		// Set it
		setAll(setOf(4, 13, 22), color)

		// Update the color and name of the toggle button in the top left of the GUI
		var button = ItemStack(RED_STAINED_GLASS)
		if (player.armorEnabled) button = ItemStack(LIME_STAINED_GLASS)
		val buttonMeta = button.itemMeta
		buttonMeta.displayName(Component.text(if (player.armorEnabled) "Enabled" else "Disabled"))
		button.itemMeta = buttonMeta
		screen.setItem(8, button)

		// Update the power indicator
		val power = player.armorPower
		val item = ItemStack(
			when {
				power >= player.maxArmorPower -> BLUE_STAINED_GLASS_PANE
				power >= (player.maxArmorPower / 4) * 3 -> GREEN_STAINED_GLASS_PANE
				power >= player.maxArmorPower / 2 -> LIME_STAINED_GLASS_PANE
				power >= player.maxArmorPower / 4 -> YELLOW_STAINED_GLASS_PANE
				power > 0 -> ORANGE_STAINED_GLASS_PANE
				else -> RED_STAINED_GLASS_PANE
			}
		)
		val meta = item.itemMeta
		meta.displayName(Component.text("Power: $power/${player.maxArmorPower}"))
		item.itemMeta = meta
		screen.setItem(17, item)
	}

	override fun onScreenUpdate() {
		updateStatus()
	}

	override fun onScreenButtonClicked(slot: Int) {
		if (slot == 8) {
			// Handle clicks on the toggle button
			player.armorEnabled = !player.armorEnabled
			updateStatus()
		}
	}

	override fun onScreenClosed() {
		// Save every module to the player, and return other items to their inventory
		playerEditableSlots.forEach {
			val item = screen.getItem(it) ?: return@forEach
			val module = item.armorModule
			if (module != null) {
				if (!player.armorModules.contains(module)) {
					player.addArmorModule(module)
					item!!.amount--
				}
			}
			player.inventory.addItem(item)
		}
	}

	override fun onPlayerChangeItem(slot: Int, oldItems: ItemStack?, newItems: ItemStack?) {
		if (slot == 26 && newItems != null && newItems.isPowerableCustomItem) {
			// Player added an item containing power to the power input slot
			val powerNeeded = player.maxArmorPower - player.armorPower
			player.armorPower += newItems.power
			newItems.power -= powerNeeded // should be automatically clamped to positive
		}
	}
}