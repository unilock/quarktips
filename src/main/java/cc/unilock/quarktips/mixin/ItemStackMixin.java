package cc.unilock.quarktips.mixin;

import cc.unilock.quarktips.QuarkTips;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static cc.unilock.quarktips.QuarkTips.CONFIG;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Inject(method = "method_17869", at = @At("TAIL"))
	private static void method_17869(List<Component> tooltipComponents, CompoundTag compoundTag, Enchantment enchantment, CallbackInfo ci) {
		if(Minecraft.getInstance().screen != null && !CONFIG.enchantingBlacklistedScreens.value().contains(Minecraft.getInstance().screen.getClass().getName())) {
			tooltipComponents.add(new QuarkTips.FakeEnchantmentComponent(enchantment));
		}
	}
}
