package net.starlegacy.feature.customitem.powerarmor.modules

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.starlegacy.StarLegacy.Companion.PLUGIN
import net.starlegacy.feature.customitem.powerarmor.PlayerPowerArmor
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


abstract class PowerArmorModule {
	abstract val item: ItemStack
	abstract val name: String
	abstract val lore: String
	abstract val weight: Int

	open fun createItem() {
		val meta = item.itemMeta
		val itemLore: MutableList<Component> = ArrayList()
		itemLore.add(Component.text(lore, NamedTextColor.DARK_GREEN))
		meta.lore(itemLore)

		meta.displayName(Component.text(name, NamedTextColor.GOLD))

		meta.persistentDataContainer.set(
			NamespacedKey(PLUGIN, "power-module-name"),
			PersistentDataType.STRING,
			name
		)

		item.itemMeta = meta
	}

	fun drainPower(player: Player, powerToDrain: Int) {
		// Drain powerToDrain power from the player, and disable if we run out
		val playerArmor = PlayerPowerArmor(player)
		if (playerArmor.armorPower <= 0) {
			// Out of power, disable the module
			disableModule(player)
		} else {
			playerArmor.armorPower -= powerToDrain
		}
	}

	open fun enableModule(player: Player) {}
	open fun disableModule(player: Player) {}
}