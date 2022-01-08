package net.starlegacy.feature.customitem.powerarmor.modules

import net.starlegacy.PLUGIN
import net.starlegacy.feature.customitem.powerarmor.armorPower
import net.starlegacy.feature.customitem.type.CustomItem
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import java.time.Instant
import java.util.UUID
import java.util.HashMap

class SpeedModule(override val weight: Int, override val customItem: CustomItem, override val effectMultiplier: Int, override val effectDuration: Int, val power: Int) :
    EffectModule(weight, customItem, PotionEffectType.SPEED, effectMultiplier, effectDuration, 0), Listener {
    // Not using an EffectModule so that it doesn't drain power while standing still
    init {
        PLUGIN.server.pluginManager.registerEvents(this, PLUGIN)
    }
    val lastMoved = HashMap<UUID, Long>()

    override fun tickModule(player: Player) {
        super.tickModule(player)
        if (hasMovedInLastSecond(player)) {
            player.armorPower -= power
        }

    }

    // onMove and hasMovedInLastSecond copied from original SL armor
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        lastMoved[event.player.uniqueId] = Instant.now().toEpochMilli()
    }
    fun hasMovedInLastSecond(player: Player): Boolean {
        return lastMoved.containsKey(player.uniqueId) && Instant.now().toEpochMilli() - (lastMoved[player.uniqueId]
            ?: 0) < 1000
    }

}