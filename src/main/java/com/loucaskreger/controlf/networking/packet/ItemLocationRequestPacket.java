package com.loucaskreger.controlf.networking.packet;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.loucaskreger.controlf.networking.Networking;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class ItemLocationRequestPacket {
	private ItemStack stack;

	public ItemLocationRequestPacket(PacketBuffer buffer) {
		this.stack = buffer.readItemStack();
	}

	public ItemLocationRequestPacket(ItemStack stack) {
		this.stack = stack;
	}

	public void toBytes(PacketBuffer buffer) {
		buffer.writeItemStack(stack);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> processRequest(() -> context.get().getSender()));
		context.get().setPacketHandled(true);
	}

	public void processRequest(Supplier<ServerPlayerEntity> player) {
		BlockPos playerPos = player.get().getPosition();
		BlockPos positiveCorner = playerPos.add(5, 3, 5);
		BlockPos negativeCorner = playerPos.add(-5, -2, -5);
		Stream<BlockPos> blocks = BlockPos.getAllInBox(positiveCorner, negativeCorner);
		Iterator<BlockPos> it = blocks.iterator();
		while (it.hasNext()) {
			BlockPos pos = it.next();
			TileEntity te = player.get().world.getTileEntity(pos);
			if (te instanceof IInventory) {
				IInventory tile = (IInventory) te;
				for (int i = 0; i < tile.getSizeInventory(); i++) {
					ItemStack stack = tile.getStackInSlot(i);
					if (this.stack.getItem() != Items.AIR && stack.getItem() == this.stack.getItem()) {
						Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
								new ItemLocationResponsePacket(pos, stack));
						break;
					}
				}
			}
		}

	}

}
