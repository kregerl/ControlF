package com.loucaskreger.controlf.networking.packet;

import java.util.function.Supplier;

import com.loucaskreger.controlf.networking.Networking;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CheckInventoryRequestPacket {
	private ItemStack stack;
	private BlockPos pos;

	public CheckInventoryRequestPacket(PacketBuffer buffer) {
		this.stack = buffer.readItemStack();
		this.pos = buffer.readBlockPos();

	}

	public CheckInventoryRequestPacket(BlockPos pos, ItemStack stack) {
		this.pos = pos;
		this.stack = stack;
	}

	public void toBytes(PacketBuffer buffer) {
		buffer.writeItemStack(this.stack);
		buffer.writeBlockPos(this.pos);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> processRequest(() -> context.get().getSender()));
		context.get().setPacketHandled(true);
	}

	public void processRequest(Supplier<ServerPlayerEntity> player) {
		TileEntity te = player.get().world.getTileEntity(pos);
		if (te instanceof IInventory) {
			IInventory tile = (IInventory) te;
			if (tile.count(this.stack.getItem()) == 0) {
				Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
						new CheckInventoryResponsePacket(this.pos, true));
			}

		}

	}

}
