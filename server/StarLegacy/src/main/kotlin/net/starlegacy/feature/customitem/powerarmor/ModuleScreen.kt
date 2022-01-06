package net.starlegacy.feature.customitem.powerarmor

import net.kyori.adventure.text.Component
import net.starlegacy.util.Screen
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class ModuleScreen(player: Player) : Screen() {
	private val red = ItemStack(Material.RED_STAINED_GLASS_PANE)
	private val green = ItemStack(Material.LIME_STAINED_GLASS_PANE)

	init {
		createScreen(player, InventoryType.CHEST, "Power Armor Modules")
		playerEditableSlots.addAll(mutableSetOf(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 26))

		setAll(mutableSetOf(5, 6, 7, 14, 15, 16, 17, 23, 24, 25), ItemStack(Material.GRAY_STAINED_GLASS_PANE))

		// Put instances of every module they have in the slots
		val slots = arrayOf(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21)
		var index = 0

		// Clear their modules, they get added back on screen close
		// Don't just set their modules to empty or the modules won't disable
		player.armorModules.forEach {
			screen.setItem(slots[index], it.item)
			player.removeArmorModule(it)
			index++
		}
		// Insert the toggle button, the fuel indicator, and the weight indicators
		updateStatus()
	}

	private fun updateStatus() {
		// Update the colored status bar that tells the player's module weight.
		// Since we temporarily removed all of their modules, we can't use PlayerPowerArmor.moduleWeight
		val slots = arrayOf(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21)
		var weight = 0
		slots.forEach {
			val module = screen.getItem(it).armorModule
			if (module != null) weight += module.weight
		}
		// Figure out what color to make the status bar
		val color = ItemStack(if (weight <= maxModuleWeight) { green } else { red })
		// Name it
		val colorMeta = color.itemMeta
		colorMeta.displayName(Component.text("Weight: $weight / $maxModuleWeight"))
		color.itemMeta = colorMeta
		// Set it
		setAll(mutableSetOf(4, 13, 22), color)

		// Update the color and name of the toggle button in the top left of the GUI
		var button = ItemStack(Material.RED_STAINED_GLASS)
		if (player.armorEnabled) button = ItemStack(Material.LIME_STAINED_GLASS)
		val buttonMeta = button.itemMeta
		buttonMeta.displayName(Component.text(if (player.armorEnabled) "Enabled" else "Disabled"))
		button.itemMeta = buttonMeta
		screen.setItem(8, button)

		// Update the power indicator
		val power = player.armorPower
		val item = ItemStack(
			when {
				power >= maxArmorPower -> Material.BLUE_STAINED_GLASS_PANE
				power >= (maxArmorPower / 4) * 3 -> Material.GREEN_STAINED_GLASS_PANE
				power >= maxArmorPower / 2 -> Material.LIME_STAINED_GLASS_PANE
				power >= maxArmorPower / 4 -> Material.YELLOW_STAINED_GLASS_PANE
				power > 0 -> Material.ORANGE_STAINED_GLASS_PANE
				else -> Material.RED_STAINED_GLASS_PANE
			}
		)
		val meta = item.itemMeta
		meta.displayName(Component.text("Power: $power/${maxArmorPower}"))
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
		if (slot == 26 && newItems != null && powerItems.containsKey(newItems.type)) {
			// Player added fuel to the power input slot
			for (i in 0..newItems.amount) {
				val currentPower = player.armorPower
				if (currentPower < maxArmorPower) {
					player.armorPower = currentPower + powerItems[newItems.type]!!
					newItems.amount--
				}
			}
		}
	}
}