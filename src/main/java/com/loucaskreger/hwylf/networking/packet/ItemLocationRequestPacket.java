package com.loucaskreger.hwylf.networking.packet;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.loucaskreger.hwylf.networking.Networking;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;

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
		BlockPos positiveCorner = playerPos.add(7, 3, 7);
		BlockPos negativeCorner = playerPos.add(-7, -2, -7);
		Stream<BlockPos> blocks = BlockPos.getAllInBox(positiveCorner, negativeCorner);
		Iterator<BlockPos> it = blocks.iterator();
		while (it.hasNext()) {
			BlockPos pos = it.next();
			TileEntity te = player.get().world.getTileEntity(pos);

			if (te != null) {

				if (te instanceof IInventory) {
					IInventory tile = (IInventory) te;
					for (int i = 0; i < tile.getSizeInventory(); i++) {
						ItemStack stack = tile.getStackInSlot(i);
						if (this.stack.getItem() != Items.AIR && stack.getItem() == this.stack.getItem()) {
							Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
									new ItemLocationResponsePacket(pos, stack));

							BlockState state = te.getBlockState();
							Block block = state.getBlock();
							if (block instanceof ChestBlock) {
								ChestBlock chest = (ChestBlock) block;
								if (state.get(ChestBlock.TYPE) != ChestType.SINGLE) {

									Direction dir = ChestBlock.getDirectionToAttached(state);
									System.out.println(dir.toString());
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
									if (secondPos != null) {
										Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
												new ItemLocationResponsePacket(secondPos, stack));
									}
								}

								break;
							}
						}
					}
				} else {
					te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
						for (int i = 0; i < handler.getSlots(); i++) {
							ItemStack handlerStack = handler.getStackInSlot(i);
							if (!handlerStack.isEmpty() && this.stack.getItem() != Items.AIR
									&& handlerStack.getItem() == this.stack.getItem()) {
								Networking.INSTANCE.send(PacketDistributor.PLAYER.with(player),
										new ItemLocationResponsePacket(pos, stack));
								break;
							}
						}
					});
				}
			}
		}

	}

}
