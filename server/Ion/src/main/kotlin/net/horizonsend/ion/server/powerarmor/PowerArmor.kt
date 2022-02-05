package net.horizonsend.ion.server.powerarmor

import java.lang.Integer.min
import java.lang.System.currentTimeMillis
import java.util.UUID
import net.horizonsend.ion.server.Ion.Companion.ionInstance
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

/**
 * Handles power armor events
 */
class PowerArmorListener : Listener {

	companion object {
		/**
		 * The players currently in combat
		 */
		val playersInCombat = mutableMapOf<UUID, Long>()

		/**
		 * The number of seconds the player must be out of combat for before they can open the module menu
		 */
		val guiCombatCooldownSeconds: Int = 20
	}

	init {
		ionInstance.server.pluginManager.registerEvents(this, StarLegacy.PLUGIN)
		ArmorActivatorRunnable().runTaskTimer(StarLegacy.PLUGIN, 2, 1)
	}

	/**
	 * Drops [event.player]'s power armor modules if [event.keepInventory] is false
	 */
	@EventHandler
	fun onPlayerDeath(event: PlayerDeathEvent) {
		// Drop the player's current power armor modules, if keepInventory is off
		if (event.keepInventory) return

		event.player.armorModules.forEach {
			event.entity.world.dropItem(event.entity.location, it.customItem.getItem())
		}
		event.player.armorModules = mutableSetOf()
		// Remove armor power
		event.player.armorPower = 0
	}

	/**
	 * Force disable the player's power armor modules.
	 * Shouldn't be necessary, but good to have just in case
	 */
	@EventHandler
	fun onPlayerDisconnect(event: PlayerQuitEvent) {
		// Shouldn't be needed, but just in case
		// Doesn't hurt anything to have it.
		event.player.armorModules.forEach { it.disableModule(event.player) }
	}

	/**
	 * If [event.entity] is a [Player], put them in the [playersInCombat] map with an updated timestamp
	 */
	@EventHandler
	fun onPlayerTakeDamage(event: EntityDamageByEntityEvent) {
		if (event.entity !is Player) return
		playersInCombat[event.entity.uniqueId] = currentTimeMillis()
	}
}

/**
 * The maximum module weight a player can have. Exceeding this value results in their modules being disabled.
 */
var maxModuleWeight = 5

/**
 * All available power armor modules should be part of this set
 */
var powerArmorModules = mutableSetOf<PowerArmorModule>()

/**
 * Whether this is a piece of power armor
 */
val ItemStack.isPowerArmor: Boolean
	get() = customItem is PowerArmorItem

/**
 * The [PowerArmorModule] this itemStack represents
 * @see getArmorModuleFromId
 */
val ItemStack.armorModule: PowerArmorModule? get() = getArmorModuleFromId(this.customItem?.id)

/**
 * @return the [PowerArmorModule] that [id] corresponds to
 * @see [ItemStack.armorModule]
 */
fun getArmorModuleFromId(id: String?): PowerArmorModule? {
	powerArmorModules.forEach { if (it.customItem.id == id) return it }
	return null
}

/**
 * Whether the player is wearing a full set of power armor
 */
val Player.isWearingPowerArmor: Boolean
	get() = inventory.helmet?.isPowerArmor ?: false &&
			inventory.chestplate?.isPowerArmor ?: false &&
			inventory.leggings?.isPowerArmor ?: false &&
			inventory.boots?.isPowerArmor ?: false

/**
 * The Player's currently equipped [PowerArmorModule]s
 *
 * Backed bt the player's PersistentDataContainer
 */
var Player.armorModules: MutableSet<PowerArmorModule>
	// The player's currently equipped modules.
	get() = persistentDataContainer.get(
			NamespacedKey(StarLegacy.PLUGIN, "current-power-armor-modules"),
			PowerModulePDC
		) ?: mutableSetOf()
	set(value) = persistentDataContainer.set(
			NamespacedKey(StarLegacy.PLUGIN, "current-power-armor-modules"),
			PowerModulePDC,
			value)


/**
 * The player's current armor power.
 *
 * Calculated as the sum of the player's current armor pieces' power values
 */
var Player.armorPower: Int
	// The current power of the player's armor. Shared between all armor pieces.
	get() {
		var power = 0
		inventory.armorContents?.forEach {
			if (it?.isPowerArmor == true) power += it.power // yet, if the player wears nothing, it is null
		}
		return power
	}
	set(value) {
		var powerLeft = value
		inventory.armorContents?.forEach {
			if (it?.isPowerArmor == true) {
				val powerToAdd = min(powerLeft, it.maxPower!!)
				it.power = powerToAdd
				powerLeft -= powerToAdd
			}
		}
	}

/**
 * The player's maximum armor power.
 *
 * Calculated as the sum of the power capacity of all the player's current armor pieces
 */
val Player.maxArmorPower: Int
	get() {
		var maxPower = 0
		inventory.armorContents?.forEach {
			if (it?.isPowerArmor == true) maxPower += it.maxPower!! // it can be null if it's empty
		}
		return maxPower
	}

/**
 * The sum of the weights of the player's current [armorModules].
 */
val Player.armorModuleWeight: Int
	get() {
		// I bet kotlin has a neater way to go about this
		var weight = 0
		armorModules.forEach {
			weight += it.weight
		}
		return weight
	}


/**
 * Whether the player has their power armor enabled.
 *
 * Backed by PersistentDataContainer
 */
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

/**
 * Makes it easier to add a module, so you don't have to do
 * val modules = [Player.armorModules]
 * modules.add(module)
 * [Player.armorModules] = modules
 * Which is very verbose and generally terrible.
 *
 * @see removeArmorModule
 */
fun Player.addArmorModule(module: PowerArmorModule) {
	armorModules = (armorModules + module).toMutableSet()
}

/**
 * Same purpose as [addArmorModule]
 * @see addArmorModule
 */
fun Player.removeArmorModule(module: PowerArmorModule) {
	// Same reason as addArmorModule
	module.disableModule(this)
	armorModules = (armorModules - module).toMutableSet()
}
