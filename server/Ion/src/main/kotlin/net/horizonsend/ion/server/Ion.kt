package net.horizonsend.ion.server

import net.starlegacy.PLUGIN
import org.dynmap.DynmapAPI
import org.dynmap.DynmapCommonAPI
import org.dynmap.DynmapCommonAPIListener

class Ion {
	companion object {
		val plugin get() = PLUGIN

		lateinit var dynmapAPI: DynmapAPI
			private set
	}

	fun onEnable() {
		DynmapCommonAPIListener.register(Listener())

		plugin.manager.apply {
			registerCommand(QuickBalance)

			commandCompletions.registerCompletion("valueNames") {
				QuickBalance.balancedValues.keys
			}
		}
	}

	class Listener: DynmapCommonAPIListener() {
		override fun apiEnabled(api: DynmapCommonAPI) {
			dynmapAPI = api as DynmapAPI
		}
	}
}