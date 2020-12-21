package com.loucaskreger.controlf.init;

import java.util.ArrayList;
import java.util.List;

import com.loucaskreger.controlf.ControlF;
import com.loucaskreger.controlf.container.ExtraSlotPlayerContainer;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerTypesInit {

	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister
			.create(ForgeRegistries.CONTAINERS, ControlF.MOD_ID);

	public static final RegistryObject<ContainerType<ExtraSlotPlayerContainer>> SLOT_CONTAINER = CONTAINER_TYPES
			.register("slot_container", () -> IForgeContainerType.create(ExtraSlotPlayerContainer::new));

}
