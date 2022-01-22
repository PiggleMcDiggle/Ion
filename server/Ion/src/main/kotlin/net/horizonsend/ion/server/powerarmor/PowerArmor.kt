package net.horizonsend.ion.server.powerarmor

import net.horizonsend.ion.server.customitems.customItem
import net.horizonsend.ion.server.customitems.types.PowerArmorItem
import net.horizonsend.ion.server.customitems.types.maxPower
import net.horizonsend.ion.server.customitems.types.power
import net.horizonsend.ion.server.powerarmor.modules.PowerArmorModule
import net.starlegacy.StarLegacy
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.lang.Integer.min
import java.time.Instant
import java.util.UUID


class PowerArmorListener : Listener {

	companion object {
		val playersInCombat = mutableMapOf<UUID, Long>()
		val guiCombatCooldownSeconds: Int = 20
	}

	init {
		StarLegacy.PLUGIN.server.pluginManager.registerEvents(this, StarLegacy.PLUGIN)
		ArmorActivatorRunnable().runTaskTimer(StarLegacy.PLUGIN, 2, 1)
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

	@EventHandler
	fun onPlayerTakeDamage(event: EntityDamageByEntityEvent) {
		if (event.entity !is Player) return
		playersInCombat[event.entity.uniqueId] = System.currentTimeMillis()
	}
}

var maxModuleWeight = 5
var powerArmorModules = mutableSetOf<PowerArmorModule>()

val ItemStack.isPowerArmor: Boolean
	get() = customItem is PowerArmorItem
val ItemStack.armorModule: PowerArmorModule? get() = getArmorModuleFromId(this.customItem?.id)
fun getArmorModuleFromId(id: String?): PowerArmorModule? {
	powerArmorModules.forEach { if (it.customItem.id == id) return it }
	return null
}


val Player.isWearingPowerArmor: Boolean
	// True if the player is wearing a full set of power armor.
	get() = inventory.helmet?.isPowerArmor ?: false &&
			inventory.chestplate?.isPowerArmor ?: false &&
			inventory.leggings?.isPowerArmor ?: false &&
			inventory.boots?.isPowerArmor ?: false


var Player.armorModules: MutableSet<PowerArmorModule>
	// The player's currently equipped modules.
	get() = persistentDataContainer.get(
			NamespacedKey(StarLegacy.PLUGIN, "current-power-armor-modules"),
			PowerModulePDC
		) ?: mutableSetOf<PowerArmorModule>()
	set(value) = persistentDataContainer.set(
			NamespacedKey(StarLegacy.PLUGIN, "current-power-armor-modules"),
			PowerModulePDC,
			value)


var Player.armorPower: Int
	// The current power of the player's armor. Shared between all armor pieces.
	get() {
		var power = 0
		inventory.armorContents.forEach {
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


val Player.maxArmorPower: Int
	get() {
		var maxPower = 0
		inventory.armorContents.forEach {
			if (it?.isPowerArmor == true) maxPower += it.maxPower!! // it can be null if it's empty
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
