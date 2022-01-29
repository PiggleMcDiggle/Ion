package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.types.CustomItem
import net.horizonsend.ion.server.customitems.types.GenericCustomItem
import net.horizonsend.ion.server.customitems.types.PowerItemBreakCanceller
import net.horizonsend.ion.server.powerarmor.PowerArmorItems
import net.horizonsend.ion.server.powerarmor.PowerArmorListener
import net.horizonsend.ion.server.powerarmor.PowerModuleItems
import net.starlegacy.PLUGIN
import org.bukkit.Bukkit.addRecipe
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
import org.bukkit.inventory.RecipeChoice
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
		 * Registers the customitem. Will warn the console if the [item]'s ID is already in use
		 * or if it's [Material], [CustomItem.model] pair is already in use
		 *
		 * @return [item]
		 */
		fun register(item: CustomItem): CustomItem {
			// Check for duplicate custom model data
			customItems.forEach { (id, customItem) ->
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

		// region Recipes
		// The original StarLegacy code had a custom addRecipe that had delays and attempts.
		// If for some reason this doesn't work as expected, check that.

		/**
		 * Registers a [ShapedRecipe] for [output]
		 *
		 * [shape] should be a series of 3 3 character strings.
		 * [map] maps the characters in [shape] to [RecipeChoice]s
		 *
		 * @return the recipe
		 * @see registerShapelessRecipe
		 */
		fun registerShapedRecipe(
			id: String, output: ItemStack, vararg shape: String, ingredients: Map<Char, RecipeChoice>
		): ShapedRecipe {
			val recipe = ShapedRecipe(NamespacedKey(PLUGIN, id), output)
			recipe.shape(*shape)
			ingredients.forEach { (char, ingredient) ->
				recipe.setIngredient(char, ingredient)
			}
			addRecipe(recipe)
			//PLUGIN.logger.warning("Created shaped recipe $id")
			return recipe
		}

		/**
		 * Registers a [ShapelessRecipe] for [output]
		 * @return the recipe
		 * @see registerShapedRecipe
		 * @see recipeChoice
		 */
		fun registerShapelessRecipe(
			id: String, output: ItemStack, vararg ingredients: RecipeChoice
		): ShapelessRecipe {
			check(ingredients.isNotEmpty())
			val recipe = ShapelessRecipe(NamespacedKey(PLUGIN, id), output)

			ingredients.forEach {
				recipe.addIngredient(it)
			}
			addRecipe(recipe)
			//PLUGIN.logger.warning("Created shapeless recipe $id")
			return recipe
		}

		/**
		 * Uses [RecipeChoice.ExactChoice]
		 * @return a [RecipeChoice] that represents [customItem].
		 */
		fun recipeChoice(customItem: CustomItem): RecipeChoice {
			return RecipeChoice.ExactChoice(customItem.getItem())
		}

		/**
		 * Uses [RecipeChoice.MaterialChoice]
		 * @return a [RecipeChoice] that represents [material]
		 */
		fun recipeChoice(material: Material): RecipeChoice {
			return RecipeChoice.MaterialChoice(material)
		}

		/**
		 * Uses [RecipeChoice.ExactChoice] which might cause issues, use the recipeChoice for [Material] if possible
		 * @return a [RecipeChoice] that represents [itemStack].
		 */
		fun recipeChoice(itemStack: ItemStack): RecipeChoice {
			return RecipeChoice.ExactChoice(itemStack) // exactchoice might cause issues?
		}

		/**
		 * @return the itemstack of the custom item or material with [id], with [count] items
		 */
		fun itemStackFromId(id: String, count: Int = 1): ItemStack? {
			return CustomItems[id]?.getItem(count) ?: ItemStack(
				Material.getMaterial(id.uppercase()) ?: return null,
				count
			)
		}
		// endregion
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

	// region Custom Item Hooks

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