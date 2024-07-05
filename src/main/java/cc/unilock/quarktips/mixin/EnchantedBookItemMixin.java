package cc.unilock.quarktips.mixin;

import cc.unilock.quarktips.QuarkTips;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin extends Item {
	public EnchantedBookItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	@NotNull
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new QuarkTips.FakeEnchantedBookComponent(stack));
	}
}
