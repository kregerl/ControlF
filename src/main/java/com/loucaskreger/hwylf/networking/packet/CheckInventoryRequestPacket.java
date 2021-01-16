package com.loucaskreger.hwylf.networking.packet;

import java.util.function.Supplier;

import com.loucaskreger.hwylf.networking.Networking;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;

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
			BlockState state = te.getBlockState();
			Block block = state.getBlock();
			if (block instanceof ChestBlock) {
				ChestBlock chest = (ChestBlock) block;
				if (state.get(ChestBlock.TYPE) != ChestType.SINGLE) {

					Direction dir = ChestBlock.getDirectionToAttached(state);
					BlockPos secondPos = null;
					switch (dir) {
					case EAST:
						secondPos = pos.add(1, 0, 0);
						break;
					case WEST:
						secondPos = pos.add(-1, 0, 0);
						break;
					case NORTH:
						secondPos = pos.add(0, 0, -1);
						break;
					case SOUTH:
						secondPos = pos.add(0, 0, 1);
						break;
					}
					TileEntity secondTileEntity = player.get().world.getTileEntity(secondPos);
					IInventory inv = (IInventory) secondTileEntity;
					if (inv.count(this.stack.getItem()) == 0 && tile.count(this.stack.getItem()) == 0) {
						Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
								new CheckInventoryResponsePacket(this.pos, true));
						Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
								new CheckInventoryResponsePacket(secondPos, true));
					}
				} else if (tile.count(this.stack.getItem()) == 0) {
					Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
							new CheckInventoryResponsePacket(this.pos, true));
				}

			} else if (tile.count(this.stack.getItem()) == 0) {
				Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
						new CheckInventoryResponsePacket(this.pos, true));
			}

		} else {
			te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
				int count = 0;
				for (int i = 0; i < handler.getSlots(); i++) {
					ItemStack handlerStack = handler.getStackInSlot(i);
					if (handlerStack.getItem() == this.stack.getItem()) {
						count++;
					}
				}
				if (count == 0) {
					Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
							new CheckInventoryResponsePacket(pos, true));
				}
			});
		}

	}

}
