package net.starlegacy.feature.customitem.type

import net.starlegacy.util.Tasks
import net.starlegacy.util.msg
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerInteractEvent

class EnergySwordItem(
	override val id: String,
	override val model: Int,
	override val displayName: String,
	override val material: Material,
) : CustomItem() {

	override fun onLeftClick(event: PlayerInteractEvent) {
		if (event.player.gameMode == GameMode.CREATIVE && event.action == Action.LEFT_CLICK_BLOCK) {
			// Prevent block breaking while in creative mode
			event.isCancelled = true
		}
		if (event.action == Action.LEFT_CLICK_BLOCK && event.action == Action.LEFT_CLICK_AIR) {
			event.player.world.playSound(event.player.location, "energy_sword.swing", 1.0f, 1.0f)
		}
	}

	override fun onHitEntity(event: EntityDamageByEntityEvent) {
		if (event.getDamage(EntityDamageEvent.DamageModifier.BASE) < 1.0f) return
		event.setDamage(EntityDamageEvent.DamageModifier.BASE, 8.0)
		event.entity.world.playSound(event.entity.location, "energy_sword.strike", 1.0f, 1.0f)
	}

	override fun onHitWhileHolding(event: EntityDamageByEntityEvent) {
		val damaged = event.entity
		if (damaged is HumanEntity && damaged.isBlocking) {
			if (damaged.getCooldown(Material.SHIELD) == 0) {
				val velocity = damaged.getVelocity()

				Tasks.syncDelay(1) { damaged.velocity = velocity }

				event.damage = 0.0
				damaged.setCooldown(Material.SHIELD, 15)
				damaged.arrowsStuck = 0
				damaged.world.playSound(damaged.location, "energy_sword.strike", 5.0f, 1.0f)
				return
			} else {
				event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0.0)
			}
		}
	}

	override fun onPrepareCraft(event: PrepareItemCraftEvent) {
		val permission = "gear.energysword." + this.id.removePrefix("energy_sword_")
		if (!event.view.player.hasPermission(permission)) {
			event.view.player msg "&cYou can only craft yellow energy swords unless you donate for other colors!"
			event.inventory.result = null
		}
	}
}