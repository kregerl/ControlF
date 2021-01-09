package com.loucaskreger.controlf.networking.packet;

import java.util.function.Supplier;

import com.loucaskreger.controlf.client.render.RenderWireframe;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class CheckSearchInventoryResponsePacket {
	public BlockPos pos;
	public boolean shouldRemove;

	public CheckSearchInventoryResponsePacket(PacketBuffer buffer) {
		this.pos = buffer.readBlockPos();
		this.shouldRemove = buffer.readBoolean();
	}

	public CheckSearchInventoryResponsePacket(BlockPos pos, boolean shouldRemove) {
		this.pos = pos;
		this.shouldRemove = shouldRemove;
	}

	public void toBytes(PacketBuffer buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeBoolean(this.shouldRemove);

	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(this::processResponse);
		context.get().setPacketHandled(true);
	}

	public void processResponse() {
		if (this.shouldRemove && RenderWireframe.searchPos.containsKey(this.pos)) {
			RenderWireframe.searchPos.remove(this.pos);
			RenderWireframe.force = false;
		}

	}
}
