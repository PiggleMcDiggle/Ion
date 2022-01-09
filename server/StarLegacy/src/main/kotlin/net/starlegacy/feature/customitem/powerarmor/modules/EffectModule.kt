package net.starlegacy.feature.customitem.powerarmor.modules

import net.starlegacy.feature.customitem.powerarmor.armorPower
import net.starlegacy.feature.customitem.type.CustomItem
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

open class EffectModule(
	override val weight: Int,
	override val customItem: CustomItem,
	private val effect: PotionEffectType,
	open val effectMultiplier: Int,
	open val effectDuration: Int,
	private val powerDrain: Int,
) : PowerArmorModule() {
	// Represents a power armor module that grants a potion effect to the player.

	override fun tickModule(player: Player) {
		super.tickModule(player)
		player.armorPower -= powerDrain
		player.addPotionEffect(
			PotionEffect(
				effect,
				effectDuration + 1, // 1 for buffer
				effectMultiplier,
				false,
				false
			)
		)
	}
}