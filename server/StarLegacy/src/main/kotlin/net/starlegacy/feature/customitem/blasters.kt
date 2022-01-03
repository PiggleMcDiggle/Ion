package net.starlegacy.feature.customitem

import net.md_5.bungee.api.ChatColor
import net.starlegacy.cache.nations.NationCache
import net.starlegacy.cache.nations.PlayerCache
import net.starlegacy.feature.customitem.CustomItems.Companion.recipeChoice
import net.starlegacy.feature.customitem.CustomItems.Companion.registerShapedRecipe
import net.starlegacy.feature.customitem.type.BlasterItem
import net.starlegacy.feature.customitem.type.EnergySwordItem
import net.starlegacy.util.Tasks
import net.starlegacy.util.enumValueOfOrNull
import net.starlegacy.util.updateMeta
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.util.Vector
import java.time.Instant
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import net.starlegacy.feature.customitem.type.power
import net.starlegacy.feature.customitem.type.uses

object BlasterItems {
	private fun registerBlaster(
		name: String,
		model: Int,
		maxPower: Int,
		maxUses: Int,
		speed: Double,
		range: Int,
		thickness: Double,
		cooldown: Int,
		power: Int,
		damage: Double,
		sound: String,
		pitchBase: Double,
		pitchRange: Double,
		explosionPower: Float? = null

	): BlasterItem {
		val item = BlasterItem(
			id = name.lowercase().replace(" ", "_"),
			displayName = name,
			material = Material.BOW,
			model = model,
			maxPower = maxPower,
			maxUses = maxUses,
			speed = speed,
			range = range,
			thickness = thickness,
			cooldown = cooldown,
			power = power,
			damage = damage,
			sound = sound,
			pitchBase = pitchBase,
			pitchRange = pitchRange,
			explosionPower = explosionPower
		)
		CustomItems.register(item)
		return item
	}

	fun register() {
		val pistol = registerBlaster(
			"Blaster Pistol",
			model = 1,
			maxPower = 2500,
			maxUses = 2500,
			speed = 1100.0,
			range = 50,
			thickness = 0.1,
			cooldown = 300,
			power = 10,
			damage = 6.0,
			sound = Sound.ENTITY_FIREWORK_ROCKET_BLAST.name,
			pitchBase = 0.7,
			pitchRange = 0.2
		)
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				pistol.id, pistol.getItem(), " bg", " t ", ingredients = mapOf(
					'b' to recipeChoice(CustomItems["battery_a"]!!),
					'g' to recipeChoice(Material.GLASS_PANE),
					't' to recipeChoice(CustomItems["titanium"]!!)
				)
			)
		}

		val rifle = registerBlaster(
			"Blaster Rifle",
			model = 2,
			maxPower = 7500,
			maxUses = 5000,
			speed = 650.0,
			range = 75,
			thickness = 0.1,
			cooldown = 600,
			power = 15,
			damage = 10.0,
			sound = "laser",
			pitchBase = 0.7,
			pitchRange = 0.3
		)
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				rifle.id, rifle.getItem(), "btg", "t  ", ingredients = mapOf(
					'b' to recipeChoice(CustomItems["battery_a"]!!),
					'g' to recipeChoice(Material.GLASS_PANE),
					't' to recipeChoice(CustomItems["titanium"]!!)
				)
			)
		}

		val sniper = registerBlaster(
			"Blaster Sniper",
			model = 3,
			maxPower = 20000,
			maxUses = 5000,
			speed = 1100.0,
			range = 250,
			thickness = 0.2,
			cooldown = 2000,
			power = 100,
			damage = 20.0,
			sound = "laser",
			pitchBase = 0.6,
			pitchRange = 0.1
		)
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				sniper.id, sniper.getItem(), " t ", "btg", "t  ", ingredients = mapOf(
					'b' to recipeChoice(CustomItems["battery_a"]!!),
					'g' to recipeChoice(Material.GLASS_PANE),
					't' to recipeChoice(CustomItems["titanium"]!!)
				)
			)
		}

		val cannon = registerBlaster(
			"Blaster Cannon",
			model = 4,
			maxPower = 25000,
			maxUses = 5000,
			speed = 450.0,
			range = 200,
			thickness = 0.2,
			cooldown = 500,
			power = 50,
			damage = 7.0,
			sound = Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST.name,
			pitchBase = 0.6,
			pitchRange = 0.1,
			explosionPower = 2.0f
		)
		Tasks.syncDelay(1) {
			registerShapedRecipe(
				cannon.id, cannon.getItem(), "brt", "t  ", ingredients = mapOf(
					'b' to recipeChoice(CustomItems["battery_a"]!!),
					'r' to recipeChoice(Material.REDSTONE),
					't' to recipeChoice(CustomItems["titanium"]!!)
				)
			)
		}
	}
}

object Blasters {
	fun getBlaster(item: ItemStack): BlasterItem? = CustomItems[item] as? BlasterItem

	private val randomColorCache = mutableMapOf<UUID, Color>()
	fun getRandomColor(uuid: UUID): Color = randomColorCache.getOrElse(uuid) {
		Random(uuid.leastSignificantBits).let { r -> Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)) }
	}

	fun getColor(player: Player): Color {
		if (player.world.name.lowercase().contains("arena")) {
			return getRandomColor(player.uniqueId)
		}

		val nation = PlayerCache[player].nation ?: return Color.BLUE
		return Color.fromRGB(NationCache[nation].color)
	}

	private val lastFired = HashMap<UUID, Long>()

	fun fireBlaster(entity: LivingEntity, blaster: ItemStack) {
		val type = getBlaster(blaster) ?: return
		val uniqueId = entity.uniqueId
		if (Instant.now().toEpochMilli() - (lastFired[uniqueId] ?: 0) < type.cooldown) {
			return
		}
		if (entity is Player) {
			val powerUsage = type.power

			if (blaster.power < powerUsage) {
				entity.sendMessage(ChatColor.RED.toString() + "Out of power!")
				return
			}

			if (!entity.getWorld().name.lowercase().contains("arena")) {
				blaster.power -= powerUsage
				blaster.uses--
			}

			entity.setCooldown(blaster.type, (type.cooldown / 1000.0f * 20.0f).toInt())
		}
		lastFired[uniqueId] = Instant.now().toEpochMilli()
		BlasterProjectile.scheduler.submit {
			val location = entity.eyeLocation

			val color = when (entity) {
				is Player -> getColor(entity)
				else -> getRandomColor(entity.uniqueId)
			}

			val dmg = type.damage
			val range = type.range
			val thickness = type.thickness
			val speed = type.speed
			val expPow = type.explosionPower
			val sound = type.sound
			val pitchB = type.pitchBase
			val pitchR = type.pitchRange
			val dir = if (entity is Player) entity.location.direction
			else entity.location.direction
				.add(Vector((Math.random() - 0.5) / 10, (Math.random() - 0.5) / 10, (Math.random() - 0.5) / 10))
				.normalize()
			BlasterProjectile(entity, location, color, dmg, range, thickness, speed, expPow, sound, pitchB, pitchR, dir)
		}
	}
}


private const val ITERATIONS_PER_BLOCK = 4f

class BlasterProjectile(
	private var shooter: Entity,
	private var location: Location,
	private var color: Color,
	private val damage: Double,
	private val range: Int,
	private val thickness: Double,
	speed: Double,
	private val explosionPower: Float?,
	sound: String,
	pitchBase: Double,
	pitchRange: Double,
	direction: Vector
) {

	companion object {
		val scheduler = Executors.newScheduledThreadPool(1, Tasks.namedThreadFactory("blasters"))
	}

	private var distance: Double = 0.0
	private val world = location.world
	private val movementPerBlock = 1.0 / ITERATIONS_PER_BLOCK
	private var velocity: Vector = direction.normalize().multiply(movementPerBlock)

	private val movementDelay = (1000 / (speed * ITERATIONS_PER_BLOCK)).toInt()

	private val offsets = listOf(
		Vector(0.0, 0.0, 0.0),
		Vector(-1.0, 0.0, 0.0),
		Vector(+1.0, 0.0, 0.0),
		Vector(0.0, -1.0, 0.0),
		Vector(0.0, +1.0, 0.0),
		Vector(0.0, 0.0, -1.0),
		Vector(0.0, 0.0, +1.0)
	).map { it.multiply(thickness) }

	init {
		scheduler.submit {
			schedule()
		}
		playSound(sound, pitchBase, pitchRange)
	}

	private fun playSound(sound: String, pitchBase: Double, pitchRange: Double) {
		val volume = 1.0f
		val pitch = (pitchBase + Math.random() * pitchRange).toFloat()

		val enumSound = enumValueOfOrNull<Sound>(sound)

		if (enumSound == null) {
			location.world.playSound(location, sound, volume, pitch)
			return
		}

		location.world.playSound(location, enumSound, volume, pitch)
	}


	private fun schedule() {
		scheduler.schedule({
			if (distance < range) {
				Tasks.sync { tick() }
				schedule()
			}
		}, movementDelay.toLong(), TimeUnit.MILLISECONDS)
	}

	private fun tick() {
		this.distance += movementPerBlock
		if (this.distance > range) {
			return
		}
		val newLocation = location.clone().add(velocity)
		val newBlockX = newLocation.blockX
		val newBlockZ = newLocation.blockZ
		if (!world.isChunkLoaded(newBlockX shr 4, newBlockZ shr 4)) {
			return
		}

		val ray = offsets.mapNotNull { offset ->
			val start = location.clone().add(offset)
			world.rayTrace(start, velocity, movementPerBlock, FluidCollisionMode.NEVER, true, 0.0)
			{ it != shooter }
		}.minByOrNull { it.hitPosition.distanceSquared(location.toVector()) }

		if (ray?.hitBlock != null) {
			this.distance = Double.MAX_VALUE
			world.playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.25f, 1.5f)
			if (explosionPower != 0f) {
				val block = location.block
				val type = block.type

				// liquid stuff is so explosions work underwater
				val liquid = block.isLiquid
				if (liquid) {
					block.setType(Material.AIR, false)
				}
				explosionPower?.let {
					world.createExplosion(
						location.toVector().midpoint(newLocation.toVector()).toLocation(world),
						explosionPower
					)
				}
				if (liquid) {
					block.setType(type, false)
				}
			}
			return
		}

		val entity: LivingEntity? = ray?.hitEntity as? LivingEntity

		if (entity == null) {
			location = newLocation
			if (distance > 0.75) {
				val particle = Particle.REDSTONE
				val dustOptions = Particle.DustOptions(color, thickness.toFloat() * 4.0f)
				world.spawnParticle(particle, location, 1, 0.0, 0.0, 0.0, 0.0, dustOptions, true)
			}
			return
		}

		var deflected = false
		if (entity is Player) {
			if (entity.isBlocking && entity.getCooldown(Material.SHIELD) == 0) {
				for (slot in arrayOf(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND)) {
					// check if sword
					CustomItems[entity.inventory.getItem(slot)] as? EnergySwordItem
						?: continue
					entity.world.playSound(entity.location, "energy_sword.strike", 5.0f, 1.0f)
					velocity = velocity.getCrossProduct(entity.location.direction).normalize()
					shooter = entity
					deflected = true
					break
				}
			}
		}

		entity.damage(damage, shooter)
		if (entity is Player) {
			(shooter as? Player)?.playSound(shooter.location, "laserhit", 1f, 0.5f)
		}

		world.playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.25f, 1.5f)
		if (!deflected) {
			distance = Double.MAX_VALUE
		}
	}
}
