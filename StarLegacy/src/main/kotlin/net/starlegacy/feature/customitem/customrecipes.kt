package net.starlegacy.feature.customitem

/*
	private fun registerWireRecipe() {
		registerShapelessRecipe(
			"wire",
			ItemStack(Material.END_ROD, 16),
			customItemChoice(CustomItems.MINERAL_COPPER),
			customItemChoice(CustomItems.MINERAL_COPPER),
			customItemChoice(CustomItems.MINERAL_COPPER)
		)
	}

	private fun registerSeaLanternRecipe() {
		registerShapelessRecipe(
			"sea_lantern",
			ItemStack(Material.SEA_LANTERN, 1),
			materialChoice(Material.PRISMARINE_CRYSTALS),
			materialChoice(Material.PRISMARINE_CRYSTALS),
			materialChoice(Material.PRISMARINE_CRYSTALS),
			materialChoice(Material.PRISMARINE_CRYSTALS)
		)
	}

	private fun registerEndPortalFrameRecipe() {
		registerShapedRecipe(
			"end_portal_frame",
			ItemStack(Material.END_PORTAL_FRAME, 1),
			"wow", "sss",
			ingredients = mapOf(
				'w' to materialChoice(Material.WARPED_PLANKS),
				'o' to materialChoice(Material.ENDER_PEARL),
				's' to materialChoice(Material.END_STONE)
			)
		)
	}

	private fun registerRocketRecipes() {
		createRecipe(
			CustomItems.ROCKET_BASE, "t t", "tht", "tgt", amount = 3, ingredients = mapOf(
				't' to customItemChoice(CustomItems.MINERAL_TITANIUM),
				'h' to customItemChoice(CustomItems.GAS_CANISTER_HELIUM),
				'g' to materialChoice(Material.HOPPER),
			)
		)

		createRecipe(
			CustomItems.ROCKET_WARHEAD_ORIOMIUM, " a ", "aoa", " a ", amount = 3, ingredients = mapOf(
				'a' to customItemChoice(CustomItems.MINERAL_ALUMINUM),
				'o' to customItemChoice(CustomItems.MINERAL_ORIOMIUM.fullBlock)
			)
		)

		createShapelessRecipe(
			CustomItems.ROCKET_ORIOMIUM,
			customItemChoice(CustomItems.ROCKET_BASE),
			customItemChoice(CustomItems.ROCKET_WARHEAD_ORIOMIUM)
		)
	}
}
*/