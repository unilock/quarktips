package cc.unilock.quarktips.mixin;

import cc.unilock.quarktips.QuarkTips;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
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
