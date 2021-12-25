package net.starlegacy.feature.customitem.type

import net.starlegacy.feature.customitem.setPower
import org.bukkit.inventory.ItemStack


abstract class PowerItem: CustomItem() {
	abstract val maxPower: Int
	override fun getItem(amount: Int): ItemStack {
		val item = super.getItem(amount)
		setPower(item, 0)
		return item
	}
}