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

class CustomItems : Listener {
	companion object {
		val customItems = mutableMapOf<String, CustomItem>()

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

		fun all(): Collection<CustomItem> = customItems.values
		fun getCustomItem(id: String?): CustomItem? = customItems[id?.lowercase()]
		fun getCustomItem(stack: ItemStack?): CustomItem? = customItems[stack?.itemMeta?.persistentDataContainer?.get(
			NamespacedKey(PLUGIN, "custom-item-id"),
			PersistentDataType.STRING
		)]

		operator fun get(id: String?): CustomItem? = getCustomItem(id)
		operator fun get(item: ItemStack?): CustomItem? = getCustomItem(item)
		val blankItem = GenericCustomItem("blank_item", 0, "Blank Custom Item", EMERALD)

		// region Recipes
		// The original StarLegacy code had a custom addRecipe that had delays and attempts.
		// If for some reason this doesn't work as expected, check that.

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

		fun recipeChoice(customItem: CustomItem): RecipeChoice {
			return RecipeChoice.ExactChoice(customItem.getItem())
		}

		fun recipeChoice(material: Material): RecipeChoice {
			return RecipeChoice.MaterialChoice(material)
		}

		fun recipeChoice(itemStack: ItemStack): RecipeChoice {
			return RecipeChoice.ExactChoice(itemStack) // exactchoice might cause issues?
		}
		// TODO: delete custom items that aren't registered from inventories

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

	@EventHandler
	fun onInteract(event: PlayerInteractEvent) {
		val item = event.item.customItem ?: return
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

	@EventHandler
	fun onDrop(event: PlayerDropItemEvent) {
		val item = event.itemDrop.itemStack.customItem ?: return
		item.onDropped(event)
	}

	@EventHandler
	fun onCraft(event: PrepareItemCraftEvent) {
		val item = event.inventory.result.customItem ?: return
		item.onPrepareCraft(event)
	}

	@EventHandler
	fun onHit(event: EntityDamageByEntityEvent) {
		val damager = event.damager as? LivingEntity ?: return
		val itemInHand = damager.equipment?.itemInMainHand ?: return
		itemInHand.customItem?.onHitEntity(event) ?: return
	}

	@EventHandler
	fun onHitWhileHolding(event: EntityDamageByEntityEvent) {
		val damaged = event.entity as? LivingEntity ?: return
		val itemInHand = damaged.equipment?.itemInMainHand ?: return
		itemInHand.customItem?.onHitWhileHolding(event) ?: return
	}

	// endregion
	// region Disable enchanting
	@Suppress("USELESS_ELVIS") // check docs for event.offers. It can be null if there's no offer at that index
	@EventHandler
	fun onTableEnchant(event: PrepareItemEnchantEvent) {
		val item = event.item.customItem ?: return
		for (i in 0..2) {
			val offer = event.offers[i] ?: continue
			if (offer.enchantment !in item.allowedEnchants) event.offers[i] = null // oh but it can be :evil_grin:
		}
	}

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

val ItemStack.isCustomItem: Boolean get() = CustomItems[this] != null
val ItemStack.customItem: CustomItem? get() = CustomItems[this]