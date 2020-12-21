package com.loucaskreger.controlf.container;

import com.loucaskreger.controlf.capability.IExtraSlotContainer;
import com.loucaskreger.controlf.container.slot.InventorySlot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.network.PacketBuffer;

public class ExtraSlotPlayerContainer extends PlayerContainer {

	public IExtraSlotContainer extraSlot;

	public ExtraSlotPlayerContainer(boolean localWorld, PlayerEntity playerIn) {
		super(playerIn.inventory, localWorld, playerIn);
		this.addSlot(new InventorySlot(extraSlot, 777, 16, 16));
	}

	public ExtraSlotPlayerContainer(int id, PlayerInventory inventory, PacketBuffer buffer) {
		this(!inventory.player.world.isRemote, inventory.player);
	}

}
