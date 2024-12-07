package cc.unilock.quarktips.mixin;

import cc.unilock.quarktips.QuarkTips;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
	@Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
	private void renderTooltip(Font font, ItemStack stack, int mouseX, int mouseY, CallbackInfo ci) {
		QuarkTips.isEnchantedBook.set(stack.is(Items.ENCHANTED_BOOK));
	}

	@ModifyVariable(method = "renderTooltipInternal", at = @At("HEAD"), argsOnly = true)
	private List<ClientTooltipComponent> renderTooltipInternal(List<ClientTooltipComponent> clientTooltipComponents) {
		return clientTooltipComponents.stream().map(clientTooltipComponent -> {
			if (clientTooltipComponent instanceof ClientTextTooltip clientTextTooltip && ((ClientTextTooltipAccessor) clientTextTooltip).getText() instanceof QuarkTips.FakeEnchantmentComponent fake) {
				return QuarkTips.convert(fake);
			}
			return clientTooltipComponent;
		}).toList();
	}
}
