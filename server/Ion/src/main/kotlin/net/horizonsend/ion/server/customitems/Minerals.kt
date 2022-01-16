package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.CustomItems.Companion.recipeChoice
import net.horizonsend.ion.server.customitems.types.CustomBlockItem
import net.horizonsend.ion.server.customitems.types.MineralItem
import org.bukkit.Material.IRON_BLOCK
import org.bukkit.Material.IRON_INGOT
import org.bukkit.Material.IRON_ORE

object Minerals {

	fun registerMineral(name: String, model: Int): MineralItem {
		val id = name.lowercase().replace(" ", "_")
		val ore = CustomBlockItem(
			id = "${id}_ore",
			displayName = "$name Ore",
			material = IRON_ORE,
			model = model,
			customBlockId = "${id}_ore"
		)
		val block = CustomBlockItem(
			"${id}_block",
			displayName = "$name Block",
			material = IRON_BLOCK,
			model = model,
			customBlockId = "${id}_block"
		)
		val item = MineralItem(
			id = id,
			displayName = name,
			material = IRON_INGOT,
			model = model,
			ore = ore,
			block = block
		)
		CustomItems.register(block)
		CustomItems.register(ore)
		CustomItems.register(item)
		// Not doing a shapeless recipe.
		// The result is the same, and I can't figure out a cleaner way to do it then just spam recipeChoice() 9 times for the ingredients
		CustomItems.registerShapedRecipe(
			block.id,
			block.getItem(),
			"aaa",
			"aaa",
			"aaa",
			ingredients = mapOf('a' to recipeChoice(item))
		)
		CustomItems.registerShapelessRecipe(item.id, item.getItem(9), ingredients = arrayOf(recipeChoice(block)))
		return item

	}

	fun register() {
		registerMineral("Aluminum", 2)
		registerMineral("Chetherite", 3)
		registerMineral("Titanium", 4)
		registerMineral("Uranium", 5)
	}
}