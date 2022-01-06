package net.starlegacy.feature.customitem.powerarmor

import net.starlegacy.StarLegacy
import net.starlegacy.feature.customitem.CustomItems
import net.starlegacy.feature.customitem.CustomItems.Companion.itemStackFromId
import net.starlegacy.feature.customitem.CustomItems.Companion.recipeChoice
import net.starlegacy.feature.customitem.CustomItems.Companion.registerShapedRecipe
import net.starlegacy.feature.customitem.customItem
import net.starlegacy.feature.customitem.powerarmor.modules.PowerArmorModule
import net.starlegacy.feature.customitem.type.PowerArmorItem
import net.starlegacy.feature.customitem.type.module.PowerModuleItem
import net.starlegacy.util.Tasks
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack


object PowerArmorItems {
	private fun registerPowerArmor(piece: String, model: Int, maxPower: Int, mat: Material): PowerArmorItem {
		val item = PowerArmorItem(
			id = "power_armor_${piece.lowercase().replace(" ", "_")}",
			displayName = "Power $piece",
			material = mat,
			model = model,
			maxPower = maxPower
		)
		CustomItems.register(item)
		return item

	}

	fun register() {
		val helmet = registerPowerArmor("Helmet", 1, 50000, Material.LEATHER_HELMET)
		val chestplate = registerPowerArmor("Chestplate", 1, 50000, Material.LEATHER_CHESTPLATE)
		val leggings = registerPowerArmor("Leggings", 1, 50000, Material.LEATHER_LEGGINGS)
		val boots = registerPowerArmor("Boots", 1, 50000, Material.LEATHER_BOOTS)
		Tasks.syncDelay(1) {
			val items = mapOf(
				'*' to recipeChoice(CustomItems["titanium"]!!),
				'b' to recipeChoice(CustomItems["battery_g"]!!)
			)
			registerShapedRecipe(helmet.id, helmet.getItem(), "*b*", "* *", ingredients = items)
			registerShapedRecipe(chestplate.id, chestplate.getItem(), "* *", "*b*", "***", ingredients = items)
			registerShapedRecipe(leggings.id, leggings.getItem(), "*b*", "* *", "* *", ingredients = items)
			registerShapedRecipe(boots.id, boots.getItem(), "* *", "*b*", ingredients = items)
		}
	}
}

object PowerModuleItems {
	private fun registerModuleItem(type: String, typeName: String, model: Int, craft: String): PowerModuleItem {
		val item = PowerModuleItem(
			id = "power_module_$type",
			displayName = "$typeName Module",
			material = Material.FLINT_AND_STEEL,
			model = model
		)
		CustomItems.register(item)
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				item.id, item.getItem(), "aga", "g*g", "aga", ingredients = mapOf(
					'a' to recipeChoice(CustomItems["aluminum"]!!),
					'g' to recipeChoice(Material.GLASS_PANE),
					'*' to recipeChoice(itemStackFromId(craft)!!)
				)
			)
		}

		return item
	}

	fun register() {
		registerModuleItem("shock_absorbing", "Shock Absorbing", 1, "titanium")
		registerModuleItem("speed_boosting", "Speed Boosting", 2, "feather")
		registerModuleItem("rocket_boosting", "Rocket Boosting", 3, "firework_rocket")
		registerModuleItem("night_vision", "Night Vision", 4, "spider_eye")
		registerModuleItem("environment", "Environment", 5, "chainmail_helmet")
		registerModuleItem("pressure_field", "Pressure Field", 6, "gas_canister_oxygen")
	}
}

class PowerArmor: Listener {
	var powerArmorModules = mutableSetOf<PowerArmorModule>()


	private lateinit var runnable: ArmorActivatorRunnable

	init {
		StarLegacy.PLUGIN.server.pluginManager.registerEvents(this, StarLegacy.PLUGIN)
	}

	@EventHandler
	fun onPlayerInteractEvent(event: PlayerInteractEvent) {
		// Bring up the power armor menu
		if (event.item.isPowerArmor) {
			ModuleScreen(event.player)
			event.isCancelled = true
		}
	}

	@EventHandler
	fun onPlayerDeath(event: PlayerDeathEvent) {
		// Drop the player's current power armor modules, if keepInventory is off
		if (event.keepInventory) return

		event.player.armorModules.forEach {
			event.entity.world.dropItem(event.entity.location, it.item)
		}
		event.player.armorModules = mutableSetOf<PowerArmorModule>()
		// Remove armor power
		event.player.armorPower = 0
	}

	@EventHandler
	fun onPlayerDisconnect(event: PlayerQuitEvent) {
		// Shouldn't be needed, but just in case
		// Doesn't hurt anything to have it.
		event.player.armorModules.forEach { it.disableModule(event.player) }
	}
}


var maxPower = 1 // The max power a set can store
var maxModuleWeight = 1
var powerItems = mutableMapOf<Material, Int>() // The items that can be placed in the GUI to power the armor

val ItemStack.isPowerArmor: Boolean
	get() = customItem is PowerArmorItem
val ItemStack.armorModule: PowerArmorModule? get() = customItem as? PowerArmorModule
