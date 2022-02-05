package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.types.CustomItem
import net.horizonsend.ion.server.customitems.types.GenericCustomItem
import net.horizonsend.ion.server.customitems.types.PowerItem
import net.horizonsend.ion.server.customitems.types.PowerItemBreakCanceller
import net.horizonsend.ion.server.customitems.types.isPowerableCustomItem
import net.horizonsend.ion.server.customitems.types.power
import net.horizonsend.ion.server.powerarmor.PowerArmorItems
import net.horizonsend.ion.server.powerarmor.PowerArmorListener
import net.horizonsend.ion.server.powerarmor.PowerModuleItems
import net.starlegacy.PLUGIN
import org.bukkit.Bukkit.addRecipe
import org.bukkit.Bukkit.getRecipe
import org.bukkit.Material
import org.bukkit.Material.EMERALD
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.persistence.PersistentDataType

/**
 * Handles [CustomItem]s
 */
class CustomItems : Listener {
	companion object {
		/**
		 * The map of [String] to [CustomItem] ids.
		 * Don't add custom items to this, use [register] instead.
		 */
		val customItems = mutableMapOf<String, CustomItem>()

		/**
		 * All custom recipes that couldn't be registered through Bukkit.
		 * Map of crafting matrices to [ItemStack]
		 * @see customShapelessRecipes
		 */
		val customShapedRecipes = mutableMapOf<List<String?>, ItemStack>()

		/**
		 * All custom shapeless recipes that couldn't be registered through Bukkit.
		 * Map of crafting matrices to [ItemStack]
		 * @see customShapedRecipes
		 */
		val customShapelessRecipes = mutableMapOf<List<String>, ItemStack>()

		/**
		 * Registers the customitem. Will warn the console if the [item]'s ID is already in use
		 * or if it's [Material], [CustomItem.model] pair is already in use
		 *
		 * @return [item]
		 */
		fun register(item: CustomItem): CustomItem {
			// Check for duplicate custom model data
			customItems.forEach { (_, customItem) ->
				if (customItem.model == item.model && customItem.material == item.material) {
					PLUGIN.logger.warning("Multiple custom items have registered for the same material and model data!")
					PLUGIN.logger.warning("${customItem.id} and ${item.id} are both using ${customItem.material.name} and ${customItem.model}")
				}
			}
			if (customItems.put(item.id, item) != null) {
				PLUGIN.logger.warning("Multiple custom items with id ${item.id} have been registered!")
			}
			item.onItemRegistered()
			//PLUGIN.logger.warning("Registered custom item ${item.id}")
			return item
		}

		/**
		 * @return a [Collection] of all the values in the [customItems] map.
		 */
		fun all(): Collection<CustomItem> = customItems.values

		/**
		 * It is recommended to use [CustomItems.get] or [ItemStack.customItem] instead for
		 * readability and clarity.
		 *
		 * @return the custom item from [id]
		 * @see [ItemStack.customItem]
		 */
		fun getCustomItem(id: String?): CustomItem? = customItems[id?.lowercase()]

		/**
		 * It is recommended to use [CustomItems.get] or [ItemStack.customItem] instead for
		 * readability and clarity.
		 *
		 * @return the custom item that [stack] represents
		 * @see [ItemStack.customItem]
		 */
		fun getCustomItem(stack: ItemStack?): CustomItem? = customItems[stack?.itemMeta?.persistentDataContainer?.get(
			NamespacedKey(PLUGIN, "custom-item-id"),
			PersistentDataType.STRING
		)]

		operator fun get(id: String?): CustomItem? = getCustomItem(id)
		operator fun get(item: ItemStack?): CustomItem? = getCustomItem(item)

		/**
		 * A blank custom item, can be used as filler or a default value
		 */
		val blankItem = GenericCustomItem("blank_item", 0, "Blank Custom Item", EMERALD)


		/**
		 * Register a shapeless recipe for [itemStack]
		 * @param ingredients the crafting ingredients as a set of id strings
		 * @see registerShapedRecipe
		 */
		fun registerShapelessRecipe(itemStack: ItemStack, ingredients: List<String>) {
			// Have to go through and check if one of the ingredients is a powerable custom item
			// If it is, ExactChoice won't work for it, so we have to do custom recipe handling
			ingredients.forEach {
				if (CustomItems[it] is PowerItem) customShapelessRecipes[ingredients] = itemStack
			}
			val key = NamespacedKey(PLUGIN, "recipe_${itemStack.id}")
			if (getRecipe(key) != null) {
				PLUGIN.logger.warning("A recipe is already registered with key ${key.key}!")
				PLUGIN.logger.warning("Cannot register bukkit shapeless recipe for ${itemStack.id}")
				return
			}
			// Either way, register a bukkit recipe
			val recipe = ShapelessRecipe(key, itemStack)
			ingredients.forEach {
				recipe.addIngredient(itemStackFromId(it)!!)
			}
			addRecipe(recipe)
			itemStack.customItem?.onRecipeRegistered(recipe)
		}

		/**
		 * Register a shaped recipe for [itemStack]
		 * @param matrix a list of item (or custom item) ids that represent the crafting grid
		 * @see registerShapelessRecipe
		 */
		fun registerShapedRecipe(itemStack: ItemStack, matrix: List<String?>) {
			// Have to go through and check if one of the ingredients is a powerable custom item
			// If it is, ExactChoice won't work for it, so we have to do custom recipe handling
			matrix.forEach {
				if (CustomItems[it] is PowerItem) customShapedRecipes[matrix] = itemStack
			}
			val key = NamespacedKey(PLUGIN, "recipe_${itemStack.id}")
			if (getRecipe(key) != null) {
				PLUGIN.logger.warning("A recipe is already registered with key ${key.key}!")
				PLUGIN.logger.warning("Cannot register bukkit shaped recipe for ${itemStack.id}")
				return
			}
			// Either way, register a bukkit recipe
			val recipe = ShapedRecipe(key, itemStack).shape("abc", "def", "ghi")
			var shape = ""
			val str = "abcdefghi"
			for (i in 0..8) {
				if (matrix[i] == null) {
					shape += " "
					continue
				}
				shape += str[i]
				recipe.setIngredient(str[i], itemStackFromId(matrix[i]!!)!!)
			}
			recipe.shape(*shape.chunked(3).toTypedArray())
			addRecipe(recipe)
			itemStack.customItem?.onRecipeRegistered(recipe)
		}

		/**
		 * @return the itemstack of the custom item or material with [id], with [count] items
		 * @see [idFromItemStack]
		 */
		fun itemStackFromId(id: String, count: Int = 1): ItemStack? {
			return CustomItems[id]?.getItem(count) ?: ItemStack(
				Material.getMaterial(id.uppercase()) ?: return null,
				count
			)
		}
	}

	init {
		PLUGIN.server.pluginManager.registerEvents(this, PLUGIN)
		PLUGIN.server.pluginManager.registerEvents(PowerItemBreakCanceller(), PLUGIN)
		PLUGIN.server.pluginManager.registerEvents(PowerArmorListener(), PLUGIN)
		// Register items here
		PlanetIcons.register()
		MiscItems.register()
		GasItems.register()
		BatteryItems.register()
		EnergySwords.register()
		BlasterItems.register()
		PowerArmorItems.register()
		PowerModuleItems.register()
		ToolItems.register()
		Minerals.register()
		MiscRecipes.register()
		RocketItems.register()
	}

	/**
	 * Handles custom crafting recipes that can't be done through Bukkit
	 */
	@EventHandler
	fun onPrepareCraft(event: PrepareItemCraftEvent) {
		// Get a crafting matrix of item ids
		val stringMatrix = List(9) { index -> event.inventory.matrix?.get(index)?.id }
		// Check shaped recipes
		if (customShapedRecipes.containsKey(stringMatrix)) event.inventory.result = customShapedRecipes[stringMatrix]
		// Check shapeless recipes
		val ingredients = stringMatrix.filterNotNull()
		customShapedRecipes.keys.forEach {
			// Pain but I don't know a  better way to compare the contents of lists
			if (it.containsAll(ingredients) && ingredients.containsAll(it)) {
				event.inventory.result = customShapelessRecipes[it]
			}
		}
		// Check if we crafted using a battery, and if so, put its power in the new item, assuming its powerable
		event.inventory.matrix!!.forEach {
			if (it?.isPowerableCustomItem == true && event.inventory.result?.isPowerableCustomItem == true) {
				event.inventory.result!!.power += it.power
			}
		}
	}

	/**
	 * Calls custom item onClick hooks.
	 *
	 * @see [CustomItem.onRightClick]
	 * @see [CustomItem.onLeftClick]
	 */
	@EventHandler
	fun onInteract(event: PlayerInteractEvent) {
		val item = event.item?.customItem ?: return
		when (event.action) {
			Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR -> {
				item.onLeftClick(event)
			}
			Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
				item.onRightClick(event)
			}
			else -> return // ugh
		}
	}

	/**
	 * Calls onDrop hooks for custom items
	 * @see [CustomItem.onDropped]
	 */
	@EventHandler
	fun onDrop(event: PlayerDropItemEvent) {
		val item = event.itemDrop.itemStack.customItem ?: return
		item.onDropped(event)
	}

	/**
	 * Calls onCraft hooks for custom items
	 * @see [CustomItem.onPrepareCraft]
	 */
	@EventHandler
	fun onCraft(event: PrepareItemCraftEvent) {
		val item = event.inventory.result?.customItem ?: return
		item.onPrepareCraft(event)
	}

	/**
	 * Calls onHit hooks for custom items
	 * @see [CustomItem.onHitEntity]
	 * @see [onHitWhileHolding]
	 */
	@EventHandler
	fun onHit(event: EntityDamageByEntityEvent) {
		val damager = event.damager as? LivingEntity ?: return
		val itemInHand = damager.equipment?.itemInMainHand ?: return
		itemInHand.customItem?.onHitEntity(event) ?: return
	}

	/**
	 * Calls onHit hooks for custom items
	 * @see [CustomItem.onHitWhileHolding]
	 * @see [onHit]
	 */
	@EventHandler
	fun onHitWhileHolding(event: EntityDamageByEntityEvent) {
		val damaged = event.entity as? LivingEntity ?: return
		val itemInHand = damaged.equipment?.itemInMainHand ?: return
		itemInHand.customItem?.onHitWhileHolding(event) ?: return
	}

	// endregion
	// region Disable enchanting
	/**
	 * Handles cancelling enchants for custom items, allows only those in [CustomItem.allowedEnchants]
	 * @see [onAnvilEnchant]
	 */
	@Suppress("USELESS_ELVIS") // check docs for event.offers. It can be null if there's no offer at that index
	@EventHandler
	fun onTableEnchant(event: PrepareItemEnchantEvent) {
		val item = event.item.customItem ?: return
		for (i in 0..2) {
			val offer = event.offers[i] ?: continue
			if (offer.enchantment !in item.allowedEnchants) event.offers[i] = null // oh but it can be :evil_grin:
		}
	}

	/**
	 * Handles cancelling enchants for custom items, allows only those in [CustomItem.allowedEnchants]
	 * @see [onTableEnchant]
	 */
	@EventHandler
	fun onAnvilEnchant(event: PrepareAnvilEvent) {
		val item = event.result?.customItem ?: return
		event.result!!.enchantments.keys.forEach {
			if (it !in item.allowedEnchants) event.result = null
			return
		}
	}
	// endregion
}

/**
 * Whether this ItemStack represents a [CustomItem]
 */
val ItemStack.isCustomItem: Boolean get() = CustomItems[this] != null

/**
 * The [customItem] this ItemStack represents, if [isCustomItem]
 * @see [CustomItems.getCustomItem]
 */
val ItemStack.customItem: CustomItem? get() = CustomItems[this]

/**
 * The id (either CustomItem or Material) of the ItemStack
 * Note: Material IDs will be lowercase
 * @see [itemStackFromId]
 */
val ItemStack.id: String
	get() = this.customItem?.id ?: this.type.toString().lowercase()