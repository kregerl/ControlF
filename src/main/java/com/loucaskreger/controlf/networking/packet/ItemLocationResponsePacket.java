package com.loucaskreger.controlf.networking.packet;

import java.util.function.Supplier;

import com.loucaskreger.controlf.client.render.RenderWireframe;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ItemLocationResponsePacket {
	public BlockPos pos;
	public ItemStack stack;

	public ItemLocationResponsePacket(PacketBuffer buffer) {
		this.pos = buffer.readBlockPos();
		this.stack = buffer.readItemStack();
	}

	public ItemLocationResponsePacket(BlockPos pos, ItemStack stack) {
		this.pos = pos;
		this.stack = stack;
	}

	public void toBytes(PacketBuffer buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeItemStack(this.stack);

	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(this::processResponse);
		context.get().setPacketHandled(true);
	}

	public void processResponse() {
		RenderWireframe.inventories.put(this.pos, this.stack);
	}

}
