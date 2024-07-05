package cc.unilock.quarktips.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;

public class QuarkTipsConfig extends ReflectiveConfig {
	public final TrackedValue<ValueList<String>> enchantingStacks = list("", "minecraft:diamond_sword", "minecraft:diamond_pickaxe", "minecraft:diamond_shovel", "minecraft:diamond_axe", "minecraft:diamond_hoe",
		"minecraft:diamond_helmet", "minecraft:diamond_chestplate", "minecraft:diamond_leggings", "minecraft:diamond_boots",
		"minecraft:shears", "minecraft:bow", "minecraft:fishing_rod", "minecraft:crossbow", "minecraft:trident", "minecraft:elytra", "minecraft:shield",
		"quark:pickarang", "supplementaries:slingshot", "supplementaries:bubble_blower", "farmersdelight:diamond_knife", "the_bumblezone:stinger_spear",
		"the_bumblezone:crystal_cannon", "the_bumblezone:honey_crystal_shield", "the_bumblezone:honey_bee_leggings_2");

	@Comment("""
		A list of additional stacks to display on each enchantment
		The format is as follows:
		enchant_id=item1,item2,item3...
		So to display a carrot on a stick on a mending book, for example, you use:
		minecraft:mending=minecraft:carrot_on_a_stick
	""")
	public final TrackedValue<ValueList<String>> enchantingAdditionalStacks = list("");

	@Comment("Screens that should not show the tooltip (bandaid fix)")
	public final TrackedValue<ValueList<String>> enchantingBlacklistedScreens = list("", "com.unascribed.exco.client.screen.TerminalScreen");
}
