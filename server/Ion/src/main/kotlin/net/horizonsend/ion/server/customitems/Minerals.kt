package net.horizonsend.ion.server.customitems

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

		CustomItems.registerShapelessRecipe(block.getItem(), List(9){item.id})
		CustomItems.registerShapelessRecipe(item.getItem(9), listOf(block.id))
		return item

	}

	fun register() {
		registerMineral("Aluminum", 2)
		registerMineral("Chetherite", 3)
		registerMineral("Titanium", 4)
		registerMineral("Uranium", 5)
	}
}