package net.horizonsend.ion.server.customitems

import net.horizonsend.ion.server.customitems.types.BlasterItem
import net.starlegacy.util.Tasks
import org.bukkit.Material.BOW
import org.bukkit.Material.GLASS_PANE
import org.bukkit.Material.REDSTONE
import org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_BLAST
import org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST

object BlasterItems {
	private fun registerBlaster(
		name: String,
		model: Int,
		maxPower: Int,
		maxUses: Int,
		speed: Double,
		range: Int,
		thickness: Double,
		cooldown: Int,
		power: Int,
		damage: Double,
		sound: String,
		pitchBase: Double,
		pitchRange: Double,
		explosionPower: Float? = null

	): BlasterItem {
		val item = BlasterItem(
			id = name.lowercase().replace(" ", "_"),
			displayName = "<red>$name",
			material = BOW,
			model = model,
			maxPower = maxPower,
			maxUses = maxUses,
			speed = speed,
			range = range,
			thickness = thickness,
			cooldown = cooldown,
			power = power,
			damage = damage,
			sound = sound,
			pitchBase = pitchBase,
			pitchRange = pitchRange,
			explosionPower = explosionPower
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		val pistol = registerBlaster(
			"Blaster Pistol",
			model = 1,
			maxPower = 2500,
			maxUses = 2500,
			speed = 1100.0,
			range = 50,
			thickness = 0.1,
			cooldown = 300,
			power = 10,
			damage = 6.0,
			sound = ENTITY_FIREWORK_ROCKET_BLAST.name,
			pitchBase = 0.7,
			pitchRange = 0.2
		)
		Tasks.syncDelay(1) {
			CustomItems.registerShapedRecipe(
				pistol.id, pistol.getItem(), " bg", " t ", ingredients = mapOf(
					'b' to CustomItems.recipeChoice(CustomItems["battery_a"]!!),
					'g' to CustomItems.recipeChoice(GLASS_PANE),
					't' to CustomItems.recipeChoice(CustomItems["titanium"]!!)
				)
			)
		}

		val rifle = registerBlaster(
			"Blaster Rifle",
			model = 2,
			maxPower = 7500,
			maxUses = 5000,
			speed = 650.0,
			range = 75,
			thickness = 0.1,
			cooldown = 600,
			power = 15,
			damage = 10.0,
			sound = "laser",
			pitchBase = 0.7,
			pitchRange = 0.3
		)
		Tasks.syncDelay(1) {
			CustomItems.registerShapedRecipe(
				rifle.id, rifle.getItem(), "btg", "t  ", ingredients = mapOf(
					'b' to CustomItems.recipeChoice(CustomItems["battery_a"]!!),
					'g' to CustomItems.recipeChoice(GLASS_PANE),
					't' to CustomItems.recipeChoice(CustomItems["titanium"]!!)
				)
			)
		}

		val sniper = registerBlaster(
			"Blaster Sniper",
			model = 3,
			maxPower = 20000,
			maxUses = 5000,
			speed = 1100.0,
			range = 250,
			thickness = 0.2,
			cooldown = 2000,
			power = 100,
			damage = 20.0,
			sound = "laser",
			pitchBase = 0.6,
			pitchRange = 0.1
		)
		Tasks.syncDelay(1) {
			CustomItems.registerShapedRecipe(
				sniper.id, sniper.getItem(), " t ", "btg", "t  ", ingredients = mapOf(
					'b' to CustomItems.recipeChoice(CustomItems["battery_a"]!!),
					'g' to CustomItems.recipeChoice(GLASS_PANE),
					't' to CustomItems.recipeChoice(CustomItems["titanium"]!!)
				)
			)
		}

		val cannon = registerBlaster(
			"Blaster Cannon",
			model = 4,
			maxPower = 25000,
			maxUses = 5000,
			speed = 450.0,
			range = 200,
			thickness = 0.2,
			cooldown = 500,
			power = 50,
			damage = 7.0,
			sound = ENTITY_FIREWORK_ROCKET_LARGE_BLAST.name,
			pitchBase = 0.6,
			pitchRange = 0.1,
			explosionPower = 2.0f
		)
		Tasks.syncDelay(1) {
			CustomItems.registerShapedRecipe(
				cannon.id, cannon.getItem(), "brt", "t  ", ingredients = mapOf(
					'b' to CustomItems.recipeChoice(CustomItems["battery_a"]!!),
					'r' to CustomItems.recipeChoice(REDSTONE),
					't' to CustomItems.recipeChoice(CustomItems["titanium"]!!)
				)
			)
		}
	}
}
