package com.loucaskreger.controlf.capability;

import com.loucaskreger.controlf.ControlF;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ExtraSlotContainerCapability extends ItemStackHandler implements IExtraSlotContainer {

	private static final Minecraft mc = Minecraft.getInstance();
	private static final int NUM_SLOTS = 1;
	private LivingEntity player;

	public ExtraSlotContainerCapability(PlayerEntity player) {
		super(NUM_SLOTS);
		this.player = player;
	}

	public ExtraSlotContainerCapability() {
		this(mc.player);

	}

	@Override
	public void setSize(int size) {
		if (size != NUM_SLOTS) {
			ControlF.LOGGER.error("Entity can only have one CTRL-F slot!");
		}
	}

}
