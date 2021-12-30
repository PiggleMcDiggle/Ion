package net.starlegacy.feature.starship

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import net.starlegacy.util.*
import org.bukkit.Material
import org.bukkit.Material.*
import java.util.*

val FLYABLE_BLOCKS: EnumSet<Material> = mutableSetOf(
	JUKEBOX, // ship computer
	NOTE_BLOCK, // used as power input/output for machines

	SPONGE, // used for lots of ship subsystems, esp. weapons

	GLASS,
	GLASS_PANE,
	IRON_BARS,

	// all 4used as thrusters
	SEA_LANTERN,
	GLOWSTONE,
	REDSTONE_LAMP,
	MAGMA_BLOCK,

	DIAMOND_BLOCK,
	REDSTONE_BLOCK,
	GOLD_BLOCK,
	LAPIS_BLOCK,
	IRON_BLOCK,
	EMERALD_BLOCK,
	BROWN_MUSHROOM_BLOCK, // custom ores

	// used for landing gears
	PISTON,
	PISTON_HEAD,
	MOVING_PISTON,
	STICKY_PISTON, // used for crate holders

	CHEST,
	ENDER_CHEST,
	TRAPPED_CHEST,
	FURNACE,
	DROPPER,
	HOPPER,
	DISPENSER,

	// misc stuff
	TORCH,
	WALL_TORCH,
	CRAFTING_TABLE,
	END_ROD,
	LEVER,
	FLOWER_POT,
	CAULDRON,
	ANVIL,
	BOOKSHELF,
	LADDER,
	DAYLIGHT_DETECTOR,
	NETHER_PORTAL,

	OBSERVER,
	REPEATER,
	COMPARATOR,
	REDSTONE_WIRE,
	REDSTONE_TORCH,
	REDSTONE_WALL_TORCH,

	LODESTONE,
	BREWING_STAND,
	LECTERN,

	END_PORTAL_FRAME,

	COPPER_BLOCK,
	EXPOSED_COPPER,
	WEATHERED_COPPER,
	OXIDIZED_COPPER,
	WAXED_COPPER_BLOCK,
	WAXED_EXPOSED_COPPER,
	WAXED_WEATHERED_COPPER,
	WAXED_OXIDIZED_COPPER,
	CUT_COPPER,
	EXPOSED_CUT_COPPER,
	WEATHERED_CUT_COPPER,
	OXIDIZED_CUT_COPPER,
	WAXED_CUT_COPPER,
	WAXED_EXPOSED_CUT_COPPER,
	WAXED_WEATHERED_CUT_COPPER,
	WAXED_OXIDIZED_CUT_COPPER,

	POLISHED_DEEPSLATE,
	COBBLED_DEEPSLATE,
	DEEPSLATE_BRICKS,
	DEEPSLATE_TILES,

	SHROOMLIGHT,
	BELL,
	GRINDSTONE,
).also {
	it.addAll(CONCRETE_TYPES)
	it.addAll(SLAB_TYPES)
	it.addAll(STAIR_TYPES)
	it.addAll(GLAZED_TERRACOTTA_TYPES)
	it.addAll(STAINED_TERRACOTTA_TYPES)
	it.addAll(WOOL_TYPES)
	it.addAll(CARPET_TYPES)
	it.addAll(STAINED_GLASS_TYPES)
	it.addAll(STAINED_GLASS_PANE_TYPES)
	it.addAll(SHULKER_BOX_TYPES)
	it.addAll(SIGN_TYPES)
	it.addAll(BUTTON_TYPES)
	it.addAll(BANNER_TYPES)
	it.addAll(DOOR_TYPES)
	it.addAll(TRAPDOOR_TYPES)
	it.addAll(PRESSURE_PLATE_TYPES)
	it.addAll(BED_TYPES)
	it.addAll(FENCE_TYPES)
	it.addAll(WALL_TYPES)
	it.addAll(PLANKS_TYPES)
}.filter { it.isBlock }.toCollection(EnumSet.noneOf(Material::class.java))

val DESTROYABLE_BLOCKS = setOf(
	SNOW,
	TALL_GRASS
)

private val FLYABLE_BLOCK_DATA_CACHE = CacheBuilder.newBuilder()
	.build<NMSBlockData, Boolean>(CacheLoader.from { blockData ->
		return@from blockData != null && FLYABLE_BLOCKS.contains(blockData.bukkitMaterial)
	})

fun isFlyable(blockData: NMSBlockData) = FLYABLE_BLOCK_DATA_CACHE[blockData]
