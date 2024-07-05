package cc.unilock.quarktips.mixin;

import cc.unilock.quarktips.QuarkTips;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

import static cc.unilock.quarktips.QuarkTips.CONFIG;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin extends Item {
	public EnchantedBookItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	@NotNull
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		if(Minecraft.getInstance().screen != null && !CONFIG.enchantingBlacklistedScreens.value().contains(Minecraft.getInstance().screen.getClass().getName())) {
			return Optional.of(new QuarkTips.FakeEnchantedBookComponent(stack));
		}
		return Optional.empty();
	}
}
