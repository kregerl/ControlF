package com.loucaskreger.controlf.networking.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.loucaskreger.controlf.client.render.RenderWireframe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

public class ItemContainingStringResponse {

	private static Minecraft mc = Minecraft.getInstance();
	private BlockPos pos;
	private ItemStack stack;
	private String string;

	public ItemContainingStringResponse(PacketBuffer buffer) {
		this.pos = buffer.readBlockPos();
		this.stack = buffer.readItemStack();
		this.string = buffer.readString();
	}

	public ItemContainingStringResponse(BlockPos pos, ItemStack stack, String string) {
		this.pos = pos;
		this.stack = stack;
		this.string = string;
	}

	public void toBytes(PacketBuffer buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeItemStack(this.stack);
		buffer.writeString(this.string);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(this::processResponse);
		context.get().setPacketHandled(true);
	}

	// Check if it is a double chest and if it is add another entry to searchPos
	// with other chest side pos

	public void processResponse() {
		if (this.string == "") {
			RenderWireframe.searchPos.clear();
			RenderWireframe.searchItemValues.clear();
			RenderWireframe.bPos = null;
			return;
		}
		System.out.println("String: " + this.string);

		RenderWireframe.searchPos.put(this.pos, this.stack);

		Iterator<Entry<BlockPos, ItemStack>> entryIterator = RenderWireframe.searchPos.entrySet().iterator();
		while (entryIterator.hasNext()) {
			Entry<BlockPos, ItemStack> entry = entryIterator.next();
			if (!stackMatches(this.string, entry.getValue())) {
				entryIterator.remove();
			} else {
				RenderWireframe.searchItemValues.add(entry.getValue().getItem());
			}
			RenderWireframe.searchPos
					.forEach((i, j) -> System.out.println("Pos: " + i.toString() + " Stack: " + j.toString()));
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
