package net.starlegacy.feature.customitem

/*

	private fun registerArmorRecipes() {
		val items = mapOf(
			'*' to customItemChoice(CustomItems.MINERAL_TITANIUM),
			'b' to customItemChoice(CustomItems.BATTERY_LARGE)
		)

		createRecipe(CustomItems.POWER_ARMOR_HELMET, "*b*", "* *", ingredients = items)
		createRecipe(CustomItems.POWER_ARMOR_CHESTPLATE, "* *", "*b*", "***", ingredients = items)
		createRecipe(CustomItems.POWER_ARMOR_LEGGINGS, "*b*", "* *", "* *", ingredients = items)
		createRecipe(CustomItems.POWER_ARMOR_BOOTS, "* *", "*b*", ingredients = items)
	}

	private fun registerModuleRecipes() = mapOf(
		CustomItems.POWER_MODULE_SHOCK_ABSORBING to customItemChoice(CustomItems.MINERAL_TITANIUM),
		CustomItems.POWER_MODULE_SPEED_BOOSTING to materialChoice(Material.FEATHER),
		CustomItems.POWER_MODULE_ROCKET_BOOSTING to materialChoice(Material.FIREWORK_ROCKET),
		CustomItems.POWER_MODULE_NIGHT_VISION to materialChoice(Material.SPIDER_EYE),
		CustomItems.POWER_MODULE_ENVIRONMENT to materialChoice(Material.CHAINMAIL_HELMET),
		CustomItems.POWER_MODULE_PRESSURE_FIELD to customItemChoice(CustomItems.GAS_CANISTER_OXYGEN)
	).forEach { (piece, center) ->
		createRecipe(
			piece, "aga", "g*g", "aga", ingredients = mapOf(
				'a' to customItemChoice(CustomItems.MINERAL_ALUMINUM),
				'g' to materialChoice(Material.GLASS_PANE),
				'*' to center
			)
		)
	}

	private fun registerSwordRecipes() = mapOf(
		CustomItems.ENERGY_SWORD_BLUE to materialChoice(Material.DIAMOND),
		CustomItems.ENERGY_SWORD_RED to materialChoice(Material.REDSTONE),
		CustomItems.ENERGY_SWORD_YELLOW to materialChoice(Material.COAL),
		CustomItems.ENERGY_SWORD_GREEN to materialChoice(Material.EMERALD),
		CustomItems.ENERGY_SWORD_PURPLE to customItemChoice(CustomItems.MINERAL_CHETHERITE),
		CustomItems.ENERGY_SWORD_ORANGE to customItemChoice(CustomItems.MINERAL_COPPER)
	).forEach { (sword, specialItem) ->
		createRecipe(
			sword, "aga", "a*a", "ata", ingredients = mapOf(
				'a' to customItemChoice(CustomItems.MINERAL_ALUMINUM),
				'g' to materialChoice(Material.GLASS_PANE),
				'*' to specialItem,
				't' to customItemChoice(CustomItems.MINERAL_TITANIUM)
			)
		)
	}

	private fun registerPowerToolRecipes() {
		createRecipe(
			CustomItems.POWER_TOOL_DRILL, "i  ", " bt", " ts", ingredients = mapOf(
				'i' to materialChoice(Material.IRON_INGOT),
				'b' to customItemChoice(CustomItems.BATTERY_MEDIUM),
				't' to customItemChoice(CustomItems.MINERAL_TITANIUM),
				's' to materialChoice(Material.STICK)
			)
		)
	}

	private fun registerGasCanisterRecipe() {
		createRecipe(
			CustomItems.GAS_CANISTER_EMPTY, " i ", "igi", " i ", ingredients = mapOf(
				'i' to customItemChoice(CustomItems.MINERAL_TITANIUM),
				'g' to materialChoice(Material.GLASS_PANE)
			)
		)
	}

	private fun registerDetonatorRecipe() {
		createRecipe(
			CustomItems.DETONATOR, " r ", "tut", " t ", ingredients = mapOf(
				'r' to materialChoice(Material.REDSTONE),
				't' to customItemChoice(CustomItems.MINERAL_TITANIUM),
				'u' to customItemChoice(CustomItems.MINERAL_URANIUM)
			)
		)
	}

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