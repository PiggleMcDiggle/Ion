package net.starlegacy.feature.customitem

/*

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


}
*/