package com.loucaskreger.hwylf.networking.packet;

import java.util.function.Supplier;

import com.loucaskreger.hwylf.client.render.RenderWireframe;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class CheckInventoryResponsePacket {
	public BlockPos pos;
	public boolean shouldRemove;

	public CheckInventoryResponsePacket(PacketBuffer buffer) {
		this.pos = buffer.readBlockPos();
		this.shouldRemove = buffer.readBoolean();
	}

	public CheckInventoryResponsePacket(BlockPos pos, boolean shouldRemove) {
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
		if (this.shouldRemove && RenderWireframe.inventoryPos.containsKey(this.pos)) {
			RenderWireframe.inventoryPos.remove(this.pos);
			RenderWireframe.force = false;
			RenderWireframe.bPos = null;
		}

	}
}
