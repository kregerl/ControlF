package com.loucaskreger.hwylf.networking.packet;

import java.util.Collection;
import java.util.function.Supplier;

import com.loucaskreger.hwylf.client.render.RenderWireframe;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
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
		RenderWireframe.inventoryPos.put(this.pos, this.stack);
		Minecraft mc = Minecraft.getInstance();
		if (mc.player.openContainer != null && !(mc.player.openContainer instanceof PlayerContainer)) {
			BlockRayTraceResult result = (BlockRayTraceResult) mc.objectMouseOver;
			BlockPos pos = result.getPos();
			RenderWireframe.bPos = pos;
		}
		Collection<ItemStack> inventoryStacks = RenderWireframe.inventoryPos.values();
		for (ItemStack iStack : inventoryStacks) {
			Item iItem = iStack.getItem();
			if (this.stack.getItem() == iItem) {
				RenderWireframe.itemValues.add(iItem);
			}
		}

	}

}
