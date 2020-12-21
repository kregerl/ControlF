package com.loucaskreger.controlf.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ExtraSlotContainerProvider implements ICapabilitySerializable<CompoundNBT> {

	@CapabilityInject(IExtraSlotContainer.class)
	public static final Capability<IExtraSlotContainer> EXTRA_SLOT = null;

	private ExtraSlotContainerCapability cap;
	private LazyOptional<IExtraSlotContainer> holder;

	public ExtraSlotContainerProvider(PlayerEntity player) {
		this.cap = new ExtraSlotContainerCapability(player);
		holder = LazyOptional.of(() -> this.cap);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return EXTRA_SLOT.orEmpty(cap, holder);
	}

	@Override
	public CompoundNBT serializeNBT() {
		return this.cap.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.cap.deserializeNBT(nbt);

	}

}
