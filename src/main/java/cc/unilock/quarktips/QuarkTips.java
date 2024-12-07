package cc.unilock.quarktips;

import cc.unilock.quarktips.config.QuarkTipsConfig;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class QuarkTips implements ClientModInitializer {
	public static final String MOD_ID = "quarktips";
	public static final QuarkTipsConfig CONFIG = QuarkTipsConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", MOD_ID, QuarkTipsConfig.class);

	private static List<ItemStack> testItems = null;
	private static Multimap<Enchantment, ItemStack> additionalStacks = null;

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("quarktips").then(literal("reload").executes(ctx -> {
				additionalStacks = null;
				testItems = null;
				return Command.SINGLE_SUCCESS;
			})));
		});
	}

	public static EnchantmentComponent convert(FakeEnchantmentComponent fake) {
		Enchantment e = fake.enchantment();

		List<ItemStack> items = getItemsForEnchantment(e);
		int itemCount = items.size();
		int lines = (int) Math.ceil((double) itemCount / 10.0);

		int len = 3 + Math.min(10, itemCount) * 9;
		return new EnchantmentComponent(len, lines * 10, e);
	}

	private static List<ItemStack> getItemsForEnchantment(Enchantment e) {
		List<ItemStack> list = new ArrayList<>();

		for (ItemStack stack : getTestItems()) {
			if (!stack.isEmpty() && e.canEnchant(stack)) {
				list.add(stack);
			}
		}

		if (getAdditionalStacks().containsKey(e)) {
			list.addAll(getAdditionalStacks().get(e));
		}

		return list;
	}

	private static Multimap<Enchantment, ItemStack> getAdditionalStacks() {
		if (additionalStacks == null) {
			computeAdditionalStacks();
		}
		return additionalStacks;
	}

	private static List<ItemStack> getTestItems() {
		if (testItems == null) {
			computeTestItems();
		}
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

		for (String s : CONFIG.enchantingAdditionalStacks.value()) {
			if (!s.contains("=")) {
				continue;
			}

			String[] tokens = s.split("=");
			String left = tokens[0];
			String right = tokens[1];

			BuiltInRegistries.ENCHANTMENT.getOptional(new ResourceLocation(left))
				.ifPresent(e -> {
					for (String itemId : right.split(",")) {
						BuiltInRegistries.ITEM.getOptional(new ResourceLocation(itemId)).ifPresent(item -> additionalStacks.put(e, new ItemStack(item)));
					}
				});
		}
	}

	public record FakeEnchantmentComponent(Enchantment enchantment) implements Component, FormattedCharSequence {
		@Override
		public Style getStyle() {
			return Style.EMPTY;
		}

		@Override
		public ComponentContents getContents() {
			return ComponentContents.EMPTY;
		}

		@Override
		public List<Component> getSiblings() {
			return Collections.emptyList();
		}

		@Override
		public FormattedCharSequence getVisualOrderText() {
			return this;
		}

		@Override
		public boolean accept(FormattedCharSink formattedCharSink) {
			return false;
		}
	}

	public record EnchantmentComponent(int width, int height, Enchantment enchantment) implements ClientTooltipComponent {
		@Override
		public void renderImage(@NotNull Font font, int tooltipX, int tooltipY, @NotNull GuiGraphics guiGraphics) {
			PoseStack pose = guiGraphics.pose();

			pose.pushPose();
			pose.translate(tooltipX, tooltipY, 0);
			pose.scale(0.5f, 0.5f, 1.0f);
			List<ItemStack> items = getItemsForEnchantment(enchantment);
			int drawn = 0;
			for (ItemStack testStack : items) {
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
