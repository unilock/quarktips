package cc.unilock.quarktips;

import cc.unilock.quarktips.config.QuarkTipsConfig;
import cc.unilock.quarktips.util.ItemNBTHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuarkTips implements ClientModInitializer {
	public static final String MOD_ID = "quarktips";
	public static final QuarkTipsConfig CONFIG = QuarkTipsConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", MOD_ID, QuarkTipsConfig.class);

	private static List<ItemStack> testItems = null;
	private static Multimap<Enchantment, ItemStack> additionalStacks = null;

	public static final String TABLE_ONLY_DISPLAY = "quark:only_show_table_enchantments";

	@Override
	public void onInitializeClient() {
		TooltipComponentCallback.EVENT.register((component) -> {
			if (component instanceof FakeEnchantedBookComponent fake) {
				EnchantmentInstance ed = getEnchantedBookEnchantment(fake.stack);
				if(ed != null) {
					boolean tableOnly = ItemNBTHelper.getBoolean(fake.stack, TABLE_ONLY_DISPLAY, false);
					List<ItemStack> items = getItemsForEnchantment(ed.enchantment, tableOnly);
					int itemCount = items.size();
					int lines = (int) Math.ceil((double) itemCount / 10.0);

					int len = 3 + Math.min(10, itemCount) * 9;
					return new EnchantedBookComponent(len, lines * 10, ed.enchantment, tableOnly);
				}
			}

			return null;
		});
	}

	private static ItemStack BOOK;

	private static List<ItemStack> getItemsForEnchantment(Enchantment e, boolean onlyForTable) {
		List<ItemStack> list = new ArrayList<>();

		for(ItemStack stack : getTestItems()) {
			if(!stack.isEmpty() && e.canEnchant(stack)) {
				if(onlyForTable && !stack.isEnchantable())
					continue;
				list.add(stack);
			}
		}

		if(onlyForTable) {
			if(BOOK == null)
				BOOK = new ItemStack(Items.BOOK);
			list.add(BOOK);
		}

		if(getAdditionalStacks().containsKey(e))
			list.addAll(getAdditionalStacks().get(e));

		return list;
	}

	private static EnchantmentInstance getEnchantedBookEnchantment(ItemStack stack) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

		if(enchantments.size() > 1) {
			return null;
		}

		for(Enchantment enchantment : enchantments.keySet()) {
			if(enchantment != null) {
				int level = enchantments.get(enchantment);
				return new EnchantmentInstance(enchantment, level);
			}
		}

		return null;
	}

	private static Multimap<Enchantment, ItemStack> getAdditionalStacks() {
		if(additionalStacks == null)
			computeAdditionalStacks();
		return additionalStacks;
	}

	public static List<ItemStack> getTestItems() {
		if(testItems == null)
			computeTestItems();
		return testItems;
	}

	private static void computeTestItems() {
		testItems = CONFIG.enchantingStacks.value().stream()
			.map(ResourceLocation::new)
			.map(BuiltInRegistries.ITEM::get)
			.filter(i -> i != Items.AIR)
			.map(ItemStack::new)
			.toList();
	}

	private static void computeAdditionalStacks() {
		additionalStacks = HashMultimap.create();

		for(String s : CONFIG.enchantingAdditionalStacks.value()) {
			if(!s.contains("="))
				continue;

			String[] tokens = s.split("=");
			String left = tokens[0];
			String right = tokens[1];

			BuiltInRegistries.ENCHANTMENT.getOptional(new ResourceLocation(left))
				.ifPresent(ench -> {
					for(String itemId : right.split(",")) {
						BuiltInRegistries.ITEM.getOptional(new ResourceLocation(itemId)).ifPresent(item -> additionalStacks.put(ench, new ItemStack(item)));
					}
				});
		}
	}

	public record FakeEnchantedBookComponent(ItemStack stack) implements TooltipComponent {}

	public record EnchantedBookComponent(int width, int height, Enchantment enchantment, boolean tableOnly) implements ClientTooltipComponent {

		@Override
		public void renderImage(@NotNull Font font, int tooltipX, int tooltipY, @NotNull GuiGraphics guiGraphics) {
			PoseStack pose = guiGraphics.pose();

			pose.pushPose();
			pose.translate(tooltipX, tooltipY, 0);
			pose.scale(0.5f, 0.5f, 1.0f);
			List<ItemStack> items = getItemsForEnchantment(enchantment, tableOnly);
			int drawn = 0;
			for(ItemStack testStack : items) {
				guiGraphics.renderItem(testStack, 6 + (drawn % 10) * 18, (drawn / 10) * 20);
				drawn++;
			}

			pose.popPose();
			RenderSystem.applyModelViewMatrix();
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public int getWidth(@NotNull Font font) {
			return width;
		}

	}
}
