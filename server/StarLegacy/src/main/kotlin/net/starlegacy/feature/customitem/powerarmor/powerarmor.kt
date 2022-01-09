package net.starlegacy.feature.customitem.powerarmor

import net.starlegacy.StarLegacy
import net.starlegacy.feature.customitem.CustomItems
import net.starlegacy.feature.customitem.CustomItems.Companion.itemStackFromId
import net.starlegacy.feature.customitem.CustomItems.Companion.recipeChoice
import net.starlegacy.feature.customitem.CustomItems.Companion.registerShapedRecipe
import net.starlegacy.feature.customitem.customItem
import net.starlegacy.feature.customitem.powerarmor.modules.EffectModule
import net.starlegacy.feature.customitem.powerarmor.modules.PowerArmorModule
import net.starlegacy.feature.customitem.powerarmor.modules.RocketModule
import net.starlegacy.feature.customitem.powerarmor.modules.SpeedModule
import net.starlegacy.feature.customitem.type.GenericCustomItem
import net.starlegacy.feature.customitem.type.PowerArmorItem
import net.starlegacy.feature.customitem.type.maxPower
import net.starlegacy.feature.customitem.type.power
import net.starlegacy.util.Tasks
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType
import java.lang.Integer.min

var maxModuleWeight = 5
var powerArmorModules = mutableSetOf<PowerArmorModule>()

val ItemStack.isPowerArmor: Boolean
	get() = customItem is PowerArmorItem
val ItemStack.armorModule: PowerArmorModule? get() = getArmorModuleFromId(this.customItem?.id)
fun getArmorModuleFromId(id: String?): PowerArmorModule? {
	powerArmorModules.forEach { if (it.customItem.id == id) return it }
	return null
}


object PowerArmorItems {
	private fun registerPowerArmor(piece: String, model: Int, mat: Material, maxPower: Int): PowerArmorItem {
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
		val helmet = registerPowerArmor("Helmet", 1, Material.LEATHER_HELMET, 50000)
		val chestplate = registerPowerArmor("Chestplate", 1, Material.LEATHER_CHESTPLATE, 50000)
		val leggings = registerPowerArmor("Leggings", 1, Material.LEATHER_LEGGINGS, 50000)
		val boots = registerPowerArmor("Boots", 1, Material.LEATHER_BOOTS, 50000)
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
	private fun registerModuleItem(type: String, typeName: String, model: Int, craft: String): GenericCustomItem {
		val item = GenericCustomItem(
			id = "power_module_$type",
			displayName = "$typeName Module",
			material = Material.FLINT_AND_STEEL,
			model = model,
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
		powerArmorModules.add(
			RocketModule(
				3,
				registerModuleItem("rocket_boosting", "Rocket Boosting", 3, "firework_rocket")
			)
		)
		powerArmorModules.add(
			SpeedModule(
				3,
				registerModuleItem("speed_boosting", "Speed Boosting", 2, "feather"),
				effectMultiplier = 1,
				effectDuration = 2,
				power = 1,
			)
		)
		powerArmorModules.add(
			EffectModule(
				1,
				registerModuleItem("night_vision", "Night Vision", 4, "spider_eye"),
				PotionEffectType.NIGHT_VISION,
				0,
				300,
				0
			)
		)
	}
}

class PowerArmor : Listener {

	init {
		StarLegacy.PLUGIN.server.pluginManager.registerEvents(this, StarLegacy.PLUGIN)
		ArmorActivatorRunnable().runTaskTimer(StarLegacy.PLUGIN, 2, 1)
	}

	@EventHandler
	fun onPlayerInteractEvent(event: PlayerInteractEvent) {
		// Bring up the power armor menu
		if (event.item?.isPowerArmor == true) {
			ModuleScreen(event.player)
			event.isCancelled = true
		}
	}

	@EventHandler
	fun onPlayerDeath(event: PlayerDeathEvent) {
		// Drop the player's current power armor modules, if keepInventory is off
		if (event.keepInventory) return

		event.player.armorModules.forEach {
			event.entity.world.dropItem(event.entity.location, it.customItem.getItem())
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

val Player.isWearingPowerArmor: Boolean
	// True if the player is wearing a full set of power armor.
	get() = inventory.helmet?.isPowerArmor ?: false &&
			inventory.chestplate?.isPowerArmor ?: false &&
			inventory.leggings?.isPowerArmor ?: false &&
			inventory.boots?.isPowerArmor ?: false


var Player.armorModules: MutableSet<PowerArmorModule>
	// The player's currently equipped modules.
	get() {
		// Load the player's modules from their PersistentDataContainer
		val moduleCSV = persistentDataContainer.get(
			NamespacedKey(StarLegacy.PLUGIN, "equipped-power-armor-modules"),
			PersistentDataType.STRING
		) ?: return mutableSetOf<PowerArmorModule>()
		val moduleIds = moduleCSV.split(",")
		val modules = mutableSetOf<PowerArmorModule>()
		moduleIds.forEach {
			val module = getArmorModuleFromId(it)
			if (module != null) {
				modules.add(module)
			}
		}
		return modules
	}
	set(value) {
		// Save the player's modules to their PersistentDataContainer
		var moduleCSV = "" // Comma separated values of all the module names

		value.forEach {
			moduleCSV += it.customItem.id + ","
		}
		persistentDataContainer.set(
			NamespacedKey(StarLegacy.PLUGIN, "equipped-power-armor-modules"),
			PersistentDataType.STRING,
			moduleCSV
		)
	}

var Player.armorPower: Int
	// The current power of the player's armor. Shared between all armor pieces.
	get() {
		var power = 0
		inventory.armorContents.forEach{
			if (it?.isPowerArmor == true) power += it.power // yet, if the player wears nothing, it is null
		}
		return power
	}
	set(value) {
		var powerLeft = value
		inventory.armorContents.forEach {
			if (it.isPowerArmor) {
				val powerToAdd = min(powerLeft, it.maxPower!!)
				it.power = powerToAdd
				powerLeft -= powerToAdd
			}
		}
	}


val Player.maxArmorPower: Int get() {
	var maxPower = 0
	inventory.armorContents.forEach{
		if (it.isPowerArmor) maxPower += it.maxPower!!
	}
	return maxPower
}

val Player.armorModuleWeight: Int
	// The player's total combined module weight
	get() {
		// I bet kotlin has a neater way to go about this
		var weight = 0
		armorModules.forEach {
			weight += it.weight
		}
		return weight
	}


var Player.armorEnabled: Boolean
	// Whether the player has enabled power armor in the GUI
	get() = this.persistentDataContainer.get(
		NamespacedKey(StarLegacy.PLUGIN, "power-armor-enabled"),
		PersistentDataType.INTEGER
	) == 1
	set(value) {
		persistentDataContainer.set(
			NamespacedKey(StarLegacy.PLUGIN, "power-armor-enabled"),
			PersistentDataType.INTEGER,
			if (value) 1 else 0
		)
	}

fun Player.addArmorModule(module: PowerArmorModule) {
	// Just makes it easier to add a module, so you don't have to do
	// val modules = PlayerArmor(player).modules
	// modules.add(module)
	// PlayerArmor(player).modules = modules
	// Which is very verbose and generally terrible.
	armorModules = (armorModules + module).toMutableSet()
}

fun Player.removeArmorModule(module: PowerArmorModule) {
	// Same reason as addArmorModule
	module.disableModule(this)
	armorModules = (armorModules - module).toMutableSet()
}
