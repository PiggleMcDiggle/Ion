package net.starlegacy.feature.customitem

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import net.starlegacy.feature.customitem.type.CustomItem
import net.starlegacy.util.set
import org.bukkit.ChatColor.GOLD
import org.bukkit.ChatColor.GRAY
import org.bukkit.ChatColor.RED
import org.bukkit.Material
import org.bukkit.Material.BOW
import org.bukkit.Material.DIAMOND_AXE
import org.bukkit.Material.DIAMOND_PICKAXE
import org.bukkit.Material.FLINT_AND_STEEL
import org.bukkit.Material.IRON_BLOCK
import org.bukkit.Material.IRON_INGOT
import org.bukkit.Material.IRON_ORE
import org.bukkit.Material.LEATHER_BOOTS
import org.bukkit.Material.LEATHER_CHESTPLATE
import org.bukkit.Material.LEATHER_HELMET
import org.bukkit.Material.LEATHER_LEGGINGS
/*

	//region Power Armor
	private fun registerPowerArmor(piece: String, pieceName: String, material: Material): PowerArmorItem = register(
		PowerArmorItem("power_armor_$piece", "${GOLD}Power$GRAY $pieceName", material, 1, 50000)
	)

	class PowerArmorItem(
		id: String,
		displayName: String,
		material: Material,
		model: Int,
		maxPower: Int
	) : PoweredCustomItem(id, displayName, material, model, true, maxPower)

	val POWER_ARMOR_HELMET = registerPowerArmor("helmet", "Helmet", LEATHER_HELMET)
	val POWER_ARMOR_CHESTPLATE = registerPowerArmor("chestplate", "Chestplate", LEATHER_CHESTPLATE)
	val POWER_ARMOR_LEGGINGS = registerPowerArmor("leggings", "Leggings", LEATHER_LEGGINGS)
	val POWER_ARMOR_BOOTS = registerPowerArmor("boots", "Boots", LEATHER_BOOTS)
	//endregion Power Armor

	//region Power Modules
	private fun registerModule(type: String, typeName: String, model: Int): PowerModuleItem =
		register(PowerModuleItem("power_module_$type", "$GRAY$typeName$GOLD Module", FLINT_AND_STEEL, model))

	class PowerModuleItem(
		id: String,
		displayName: String,
		material: Material,
		model: Int
	) : CustomItem(id, displayName, material, model, true)

	val POWER_MODULE_SHOCK_ABSORBING = registerModule("shock_absorbing", "Shock Absorbing", 1)
	val POWER_MODULE_SPEED_BOOSTING = registerModule("speed_boosting", "Speed Boosting", 2)
	val POWER_MODULE_ROCKET_BOOSTING = registerModule("rocket_boosting", "Rocket Boosting", 3)
	val POWER_MODULE_NIGHT_VISION = registerModule("night_vision", "Night Vision", 4)
	val POWER_MODULE_ENVIRONMENT = registerModule("environment", "Environment", 5)
	val POWER_MODULE_PRESSURE_FIELD = registerModule("pressure_field", "Pressure Field", 6)
	//endregion Power Modules

	//region Power Tools
	private fun registerPowerTool(type: String, name: String, mat: Material, model: Int, maxPower: Int) =
		makePoweredItem("power_tool_$type", "${GOLD}Power$GRAY $name", mat, model, maxPower)

	val POWER_TOOL_DRILL = registerPowerTool("drill", "Drill", DIAMOND_PICKAXE, 1, 50000)

	init {
		idMap["power_tool_pickaxe"] = POWER_TOOL_DRILL
	}

	val POWER_TOOL_CHAINSAW = registerPowerTool("chainsaw", "Chainsaw", DIAMOND_AXE, 1, 100000)
	//endregion Power Tools

	//region Minerals
	class MineralCustomItem(
		id: String,
		displayName: String,
		material: Material,
		model: Int,
		val ore: CustomBlockItem,
		val fullBlock: CustomBlockItem
	) : CustomItem(id, displayName, material, model, false)

	private fun registerMineral(type: String, typeName: String, model: Int): MineralCustomItem {
		return register(
			MineralCustomItem(
				id = type,
				displayName = typeName,
				material = IRON_INGOT,
				model = model,
				ore = makeBlockItem(
					id = "${type}_ore",
					displayName = "$typeName Ore",
					material = IRON_ORE,
					model = model,
					blockId = "${type}_ore"
				),
				fullBlock = makeBlockItem(
					id = "${type}_block",
					displayName = "$typeName Block",
					material = IRON_BLOCK,
					model = model,
					blockId = "${type}_block"
				)
			)
		)
	}

	val MINERAL_COPPER = registerMineral(type = "copper", typeName = "Copper", model = 1)
	val MINERAL_ALUMINUM = registerMineral(type = "aluminum", typeName = "Aluminum", model = 2)
	val MINERAL_CHETHERITE = registerMineral(type = "chetherite", typeName = "Chetherite", model = 3)
	val MINERAL_TITANIUM = registerMineral(type = "titanium", typeName = "Titanium", model = 4)
	val MINERAL_URANIUM = registerMineral(type = "uranium", typeName = "Uranium", model = 5)
	val MINERAL_ORIOMIUM = registerMineral("oriomium", "Oriomium", 6)
	//endregion Minerals


	//region Rockets
	val ROCKET_BASE = makeItem("rocket_base", "Rocket Base", Material.STICK, 1)

	val ROCKET_WARHEAD_ORIOMIUM = makeItem("rocket_warhead_oriomium", "Oriomium Warhead", Material.STICK, 2)

	val ROCKET_ORIOMIUM = makeItem("rocket_oriomium", "Oriomium Rocket", Material.STICK, 3)
}
*/