package cc.unilock.quarktips.util;

import net.minecraft.world.item.ItemStack;

public class ItemNBTHelper {
	public static boolean verifyExistence(ItemStack stack, String tag) {
		return !stack.isEmpty() && stack.hasTag() && stack.getOrCreateTag().contains(tag);
	}

	public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getBoolean(tag) : defaultExpected;
	}
}
