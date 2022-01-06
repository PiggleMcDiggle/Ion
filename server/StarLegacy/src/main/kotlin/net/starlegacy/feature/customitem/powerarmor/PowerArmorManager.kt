package net.starlegacy.feature.customitem.powerarmor

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.starlegacy.StarLegacy.Companion.PLUGIN
import net.starlegacy.feature.customitem.powerarmor.modules.PowerArmorModule
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType

class PowerArmorManager : Listener {
	// Utility functions for dealing with power armor
	// + create power armor itself from the config file

	companion object {
		var powerArmorModules = mutableSetOf<PowerArmorModule>()

		// These are all overwritten by the config on init
		var maxPower = 1 // The max power a set can store
		var maxModuleWeight = 1
		var powerItems = mutableMapOf<Material, Int>() // The items that can be placed in the GUI to power the armor

		fun isPowerArmor(armor: ItemStack?): Boolean {
			if (armor == null) return false
			return armor.itemMeta.persistentDataContainer.get(
				NamespacedKey(PLUGIN, "is-power-armor"),
				PersistentDataType.INTEGER
			) != null
		}

		fun getModuleFromItemStack(item: ItemStack?): PowerArmorModule? {
			if (item == null) return null
			return getModuleFromName(
				item.itemMeta.persistentDataContainer.get(
					NamespacedKey(
						PLUGIN,
						"power-module-name"
					), PersistentDataType.STRING
				)
			)
		}

		fun getModuleFromName(name: String?): PowerArmorModule? {
			powerArmorModules.forEach {
				if (it.name == name) {
					return it
				}
			}
			return null
		}
	}

	private lateinit var chestplate: ItemStack
	private lateinit var leggings: ItemStack
	private lateinit var boots: ItemStack
	private lateinit var helmet: ItemStack

	private lateinit var runnable: ArmorActivatorRunnable

	init {
		PLUGIN.server.pluginManager.registerEvents(this, PLUGIN)
		reloadPowerArmor()
	}

	private fun loadRecipe(key: NamespacedKey, item: ItemStack, path: String, items: Map<Char, Material> = mapOf()) {
		// Load and register the recipe in the config at path
		// Items represents any characters that have a predefined item
		// Recipe format:
		// <path> :
		// 		layout:
		// 			- "ccc"
		// 			- "ccc"
		// 			- "ccc"
		// 		items:
		//			c: SEA_PICKLE

		// If an old recipe exists with this key, remove it
		PLUGIN.server.removeRecipe(key)

		val recipe = ShapedRecipe(key, item)
		recipe.shape(
			*PLUGIN.config.getStringList("$path.layout").toTypedArray()
		)
		for (craftItemKey in PLUGIN.config.getConfigurationSection(
			"$path.items"
		)!!.getKeys(false)) {
			// For each key, add key, item to the recipe
			recipe.setIngredient(
				craftItemKey[0],
				Material.getMaterial(PLUGIN.config.getString("$path.items.$craftItemKey")!!)!!
			)
		}
		items.forEach { (c, m) ->
			recipe.setIngredient(c, m)
		}
		Bukkit.addRecipe(recipe)
	}

	private fun loadModules() {
		// Load the modules from the config
		powerArmorModules = mutableSetOf() // clear the modules

		PLUGIN.config.getConfigurationSection("powerArmor.modules")!!.getKeys(false).forEach {
			// First, determine whether its a hardcoded module or an effect module
			val type = PLUGIN.config.getString("powerArmor.modules.$it.type")!!
			val newModule: PowerArmorModule
			when (type) {
				"EFFECT" -> {
					// Effect module, load all the stuff from the "effect" config section
					// and create a new module
					newModule = EffectModule(
						ItemStack(Material.getMaterial(PLUGIN.config.getString("powerArmor.modules.$it.material")!!)!!),
						it,
						PLUGIN.config.getString("powerArmor.modules.$it.lore")!!,
						PLUGIN.config.getInt("powerArmor.modules.$it.weight"),
						PotionEffectType.getByName(PLUGIN.config.getString("powerArmor.modules.$it.effect.id")!!)!!,
						PLUGIN.config.getInt("powerArmor.modules.$it.effect.multiplier"),
						PLUGIN.config.getInt("powerArmor.modules.$it.effect.durationBonus"),
						PLUGIN.config.getInt("powerArmor.modules.$it.effect.powerDrain"),
						PLUGIN.config.getInt("powerArmor.modules.$it.effect.period")
					)
				}
				else -> {
					PLUGIN.logger.warning("Unknown module type: $type")
					return@forEach // no specified type, move on.
				}
			}

			newModule.createItem()
			loadRecipe(
				NamespacedKey(PLUGIN, "power-module-${newModule.name.replace(" ", "-")}"),
				newModule.item,
				"powerArmor.modules.$it.recipe"
			)
			powerArmorModules.add(newModule)
		}
	}


	private fun loadArmor() {
		// Reset the armor, and load it from the config
		chestplate = ItemStack(Material.LEATHER_CHESTPLATE)
		leggings = ItemStack(Material.LEATHER_LEGGINGS)
		boots = ItemStack(Material.LEATHER_BOOTS)
		helmet = ItemStack(Material.LEATHER_HELMET)

		mutableSetOf(helmet, chestplate, leggings, boots).forEach {
			val meta = it.itemMeta as LeatherArmorMeta
			val lore: MutableList<Component> = ArrayList()
			lore.add(Component.text(PLUGIN.config.getString("powerArmor.lore")!!, NamedTextColor.DARK_GREEN))
			meta.lore(lore)

			// I'm not going to say I like this logic, but it works.
			// This bothers me, I really, really want to use capitalize()
			val typeName = it.type.toString().split("_")[1].lowercase().replaceFirstChar { char -> char.titlecase() }
			meta.displayName(Component.text("Power $typeName", NamedTextColor.GOLD))

			meta.persistentDataContainer.set(NamespacedKey(PLUGIN, "is-power-armor"), PersistentDataType.INTEGER, 1)
			it.itemMeta = meta
			loadRecipe(
				NamespacedKey(PLUGIN, "power-$typeName"),
				it,
				"powerArmor.recipe",
				mutableMapOf("a"[0] to it.type)
			)
		}
	}


	private fun reloadPowerArmor() {
		// Reset everything and load it from the config again
		powerItems = mutableMapOf() // clear the power items
		if (this::runnable.isInitialized) runnable.cancel() // cancel the runnable, the interval might have changed

		// Load some base values
		maxModuleWeight = PLUGIN.config.getInt("powerArmor.maxModuleWeight")
		maxPower = PLUGIN.config.getInt("powerArmor.maxPower")
		PLUGIN.config.getConfigurationSection("powerArmor.powerItems")!!.getKeys(false).forEach {
			powerItems.putIfAbsent(Material.getMaterial(it)!!, PLUGIN.config.getInt("powerArmor.powerItems.$it"))
		}

		loadArmor()
		loadModules()

		// Check once per interval defined in config for players wearing power armor
		ArmorActivatorRunnable().runTaskTimer(PLUGIN, 5, PLUGIN.config.getLong("powerArmor.updateInterval"))
	}


	@EventHandler
	fun onPlayerInteractEvent(event: PlayerInteractEvent) {
		// Bring up the power armor menu
		if (isPowerArmor(event.item)) {
			ModuleScreen(event.player)
			event.isCancelled = true
		}
	}

	@EventHandler
	fun onPlayerDeath(event: PlayerDeathEvent) {
		// Drop the player's current power armor modules, if keepInventory is off
		if (event.keepInventory) return
		val playerArmor = PlayerPowerArmor(event.entity)
		playerArmor.modules.forEach {
			event.entity.world.dropItem(event.entity.location, it.item)
		}
		playerArmor.modules = mutableSetOf<PowerArmorModule>()
		// Remove armor power
		playerArmor.armorPower = 0
	}

	@EventHandler
	fun onPlayerDisconnect(event: PlayerQuitEvent) {
		// Shouldn't be needed, but just in case
		// Doesn't hurt anything to have it.
		PlayerPowerArmor(event.player).modules.forEach { it.disableModule(event.player) }
	}
}