package net.starlegacy.feature.multiblock.particleshield

import net.starlegacy.feature.multiblock.MultiblockShape
import net.starlegacy.feature.progression.advancement.SLAdvancement

object ShieldMultiblockClass08i : SphereShieldMultiblock() {
	override val advancement = SLAdvancement.PARTICLE_SHIELD_08I
	override val signText = createSignText(
		line1 = "&3Particle Shield",
		line2 = "&7Generator",
		line3 = null,
		line4 = "&8Class &d0.8i"
	)

	override val maxRange = 10
	override val isReinforced: Boolean = true

	override fun MultiblockShape.buildStructure() {
		z(+0) {
			y(-1) {
				x(-1).sponge()
				x(+0).chetheriteBlock()
				x(+1).sponge()
			}

			y(+0) {
				x(-1).anyGlassPane()
				x(+0).anyGlass()
				x(+1).anyGlassPane()
			}
		}
	}
}
