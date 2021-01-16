package com.loucaskreger.hwylf.networking.packet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.loucaskreger.hwylf.client.render.RenderWireframe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

public class RemoveItemsNotContainingStringResponse {

	private static final Minecraft mc = Minecraft.getInstance();
	private String string;

	public RemoveItemsNotContainingStringResponse(PacketBuffer buffer) {
		this.string = buffer.readString();
	}

	public RemoveItemsNotContainingStringResponse(String string) {
		this.string = string;
	}

	public void toBytes(PacketBuffer buffer) {
		buffer.writeString(this.string);

	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(this::process);
		context.get().setPacketHandled(true);
	}

	public void process() {
		RenderWireframe.force = true;

		Iterator<Entry<BlockPos, ItemStack>> entryIterator = RenderWireframe.searchPos.entrySet().iterator();
		while (entryIterator.hasNext()) {
			Entry<BlockPos, ItemStack> entry = entryIterator.next();
			if (!stackMatches(this.string, entry.getValue())) {
				entryIterator.remove();
			}
		}

		Iterator<Item> itemIterator = RenderWireframe.searchItemValues.iterator();
		while (itemIterator.hasNext()) {
			Item item = itemIterator.next();
			if (!stackMatches(this.string, item)) {
				itemIterator.remove();
			}
		}
	}

	public static boolean stackMatches(String text, Item item) {
		return stackMatches(text, new ItemStack(item));
	}

	public static boolean stackMatches(String text, ItemStack stack) {
		if (stack.getItem().equals(Items.AIR)) {
			return false;
		}
		ArrayList<String> keys = new ArrayList<String>();
		for (ITextComponent line : stack.getTooltip(mc.player,
				mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL)) {
			keys.add(line.getString());
		}
		for (String key : keys) {
			if (key.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
				return true;
			}
		}
		return false;
	}

}
