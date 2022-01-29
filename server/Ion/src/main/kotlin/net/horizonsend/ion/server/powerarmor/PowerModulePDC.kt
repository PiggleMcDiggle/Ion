package net.horizonsend.ion.server.powerarmor

import net.horizonsend.ion.server.powerarmor.modules.PowerArmorModule
import net.starlegacy.PLUGIN
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.persistence.PersistentDataType.STRING

/**
 * Stores a [MutableSet] of [PowerArmorModule] to a PersistentDataContainer
 */
object PowerModulePDC : PersistentDataType<Array<PersistentDataContainer>, MutableSet<PowerArmorModule>> {
	override fun getPrimitiveType(): Class<Array<PersistentDataContainer>> {
		return Array<PersistentDataContainer>::class.java
	}

	override fun getComplexType(): Class<MutableSet<PowerArmorModule>> {
		return mutableSetOf<PowerArmorModule>().javaClass
	}

	override fun toPrimitive(
		complex: MutableSet<PowerArmorModule>,
		context: PersistentDataAdapterContext
	): Array<PersistentDataContainer> {
		val pdc = mutableSetOf<PersistentDataContainer>()
		complex.forEach {
			val newPDC = context.newPersistentDataContainer()
			newPDC.set(NamespacedKey(PLUGIN, "module-item-id"), STRING, it.customItem.id)
			pdc.add(newPDC)
		}
		return pdc.toTypedArray()
	}

	override fun fromPrimitive(
		primitive: Array<PersistentDataContainer>,
		context: PersistentDataAdapterContext
	): MutableSet<PowerArmorModule> {
		val modules = mutableSetOf<PowerArmorModule>()
		primitive.forEach {
			val module = getArmorModuleFromId(it.get(NamespacedKey(PLUGIN, "module-item-id"), STRING))
			if (module != null) {
				modules.add(module)
			}
		}
		return modules
	}
}