package net.starlegacy.feature.customitem.type

import net.starlegacy.StarLegacy.Companion.PLUGIN
import net.starlegacy.feature.customitem.Blasters
import org.bukkit.DyeColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.material.Colorable

class BlasterItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
	override val maxPower: Int,
	override val maxUses: Int,
	val speed: Double,
	val range: Int,
	val thickness: Double,
	val cooldown: Int,
	val power: Int,
	val damage: Double,
	val sound: String,
	val pitchBase: Double,
	val pitchRange: Double,
	val explosionPower: Float? = null
) : BreakablePowerItem(), Listener {

	init {
		PLUGIN.server.pluginManager.registerEvents(this, PLUGIN)
	}

	override fun onLeftClick(event: PlayerInteractEvent) {
		Blasters.fireBlaster(event.player, event.item!!)
	}

	// The rest of this is copied from the old SL BlasterListener.kt

	@EventHandler
	fun onFireBlaster(event: EntityShootBowEvent) {
		val entity = event.entity
		val bow = event.bow ?: return

		if (entity is Player && entity.gameMode == GameMode.SPECTATOR) {
			return
		}

		val blaster = Blasters.getBlaster(bow) ?: return

		event.isCancelled = true
		Blasters.fireBlaster(entity, bow)
	}

	override fun onPrepareCraft(event: PrepareItemCraftEvent) {
		var color: DyeColor? = null
		var dye: ItemStack? = null
		for (item: ItemStack? in event.inventory.matrix) {
			if (item != null && item.data is Colorable) {
				color = (item.data as Colorable).color
				dye = item
				break
			}
		}

		if (dye == null || color == null) {
			return
		}

		for (item: ItemStack? in event.inventory.matrix) {
			if (item == null) {
				continue
			}

			Blasters.getBlaster(item) ?: continue
			val lore = item.lore ?: mutableListOf()

			if (lore.size < 2) {
				lore.add(color.name)
			} else {
				lore[1] = color.name
			}

			if (lore == item.lore) {
				return
			}

			if (item.lore == lore) {
				return
			}

			item.lore = lore
			dye.amount = dye.amount - 1
			return
		}
	}

	@EventHandler
	fun onEntityDamage(event: EntityDamageByEntityEvent) {
		val entity = event.entity
		val damager = event.damager
		if (entity is Monster && (damager is Monster || damager is Projectile && damager.shooter is Monster)) {
			event.isCancelled = true
		}
	}
}