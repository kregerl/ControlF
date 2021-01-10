package com.loucaskreger.hwylf.networking.packet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.loucaskreger.hwylf.networking.Networking;
import com.loucaskreger.hwylf.util.ChestUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class FindItemsContainingStringRequest {

	private static Minecraft mc = Minecraft.getInstance();
	private String string;

	public FindItemsContainingStringRequest(PacketBuffer buffer) {
		this.string = buffer.readString();
	}

	public FindItemsContainingStringRequest(String string) {
		this.string = string;
	}

	public void toBytes(PacketBuffer buffer) {
		buffer.writeString(this.string);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> processRequest(() -> context.get().getSender()));
		context.get().setPacketHandled(true);
	}

	public void processRequest(Supplier<ServerPlayerEntity> player) {
		if (this.string == "") {
			Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player), new ResetRendersPacket());
			return;
		}
		BlockPos playerPos = player.get().getPosition();
		BlockPos positiveCorner = playerPos.add(5, 3, 5);
		BlockPos negativeCorner = playerPos.add(-5, -2, -5);
		Stream<BlockPos> blocks = BlockPos.getAllInBox(positiveCorner, negativeCorner);
		Iterator<BlockPos> it = blocks.iterator();
		while (it.hasNext()) {
			BlockPos pos = it.next();
			TileEntity te = player.get().world.getTileEntity(pos);
			BlockState state = player.get().world.getBlockState(pos);

			if (te != null) {
				if (te instanceof IInventory) {
					IInventory tile = (IInventory) te;
					for (int i = 0; i < tile.getSizeInventory(); i++) {
						ItemStack stack = tile.getStackInSlot(i);
						if (stackMatches(this.string, stack)) {
							// If NONE of the items match, remove it from the map
							Block block = state.getBlock();
							if (block instanceof ChestBlock) {
								if (state.get(ChestBlock.TYPE) != ChestType.SINGLE) {

									BlockPos attachedPos = ChestUtil.getAttachedChest(state, pos);
									if (!attachedPos.equals(pos)) {
										Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
												new ItemContainingStringResponse(attachedPos, stack, this.string));
									}
								}
							}
							Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
									new ItemContainingStringResponse(pos, stack, this.string));
						} else {
							Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
									new RemoveItemsNotContainingStringResponse(this.string));
						}
						// else { If none of the item match, send a different packet containing only the
						// string and check all blockPos in searchPosand items in searchItemValues
						// remove if dont match new string}
					}
				}
			}
		}

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
