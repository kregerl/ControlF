package com.loucaskreger.hwylf.networking.packet;

import java.util.function.Supplier;

import com.loucaskreger.hwylf.client.render.RenderWireframe;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ResetRendersPacket {

	public ResetRendersPacket(PacketBuffer buffer) {
	}

	public ResetRendersPacket() {
	}

	public void toBytes(PacketBuffer buffer) {
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(this::clearRenderList);
		context.get().setPacketHandled(true);
	}

	private void clearRenderList() {
		RenderWireframe.inventoryPos.clear();
		RenderWireframe.itemValues.clear();
		RenderWireframe.searchPos.clear();
		RenderWireframe.searchItemValues.clear();
		RenderWireframe.bPos = null;
		RenderWireframe.force = false;
	}

}
