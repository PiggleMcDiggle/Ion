package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.types.GenericCustomItem
import net.starlegacy.util.Tasks
import org.bukkit.Material.DIAMOND_BLOCK
import org.bukkit.Material.HOPPER
import org.bukkit.Material.STICK

object RocketItems {
	fun register() {
		val base = CustomItems.register(
			GenericCustomItem(
				id = "rocket_base",
				displayName = "Rocket Base",
				material = STICK,
				model = 1
			)
		)
		val warhead = CustomItems.register(
			GenericCustomItem(
				id = "rocket_warhead_oriomium",
				displayName = "Oriomium Warhead",
				material = STICK,
				model = 2
			)
		)
		val rocket = CustomItems.register(
			GenericCustomItem(
				id = "rocket_oriomium",
				displayName = "Oriomium Rocket",
				material = STICK,
				model = 3
			)
		)
		Tasks.syncDelay(1) {
			CustomItems.registerShapelessRecipe(
				rocket.getItem(),
				setOf(base.id, warhead.id)
			)
			CustomItems.registerShapedRecipe(
				base.getItem(3),
				listOf(
					"titanium", null,                  "titanium",
					"titanium", "gas_canister_helium", "titanium",
					"titanium", "hopper",              "titanium"
				)
			)
			CustomItems.registerShapedRecipe(
				warhead.getItem(3),
				listOf(
					null,       "aluminum",      null,
					"aluminum", "diamond_block", "aluminum",
					null,       "aluminum",      null
				)
			)
		}
	}
}