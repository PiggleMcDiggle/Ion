package net.horizonsend.ion.server.powerarmor.modules

import net.horizonsend.ion.server.customitems.types.CustomItem
import net.horizonsend.ion.server.powerarmor.armorPower
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Represents an armor module that applies a potion effect to the player every module tick
 */
open class PotionEffectModule(
	override val weight: Int,
	override val customItem: CustomItem,
	/**
	 * The PotionEffectType to grant the player.
	 */
	private val effect: PotionEffectType,
	/**
	 * The multiplier for [effect].
	 *
	 * Note that a value of 0 is effect level 1, 1 is level 2, etc.
	 */
	open val effectMultiplier: Int,
	/**
	 * Extra duration to add to the effect. Useful for effects such as Night Vision.
	 */
	open val effectDuration: Int,
	/**
	 * The [Player.armorPower] to drain each module tick.
	 */
	private val powerDrain: Int,
) : PowerArmorModule() {

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