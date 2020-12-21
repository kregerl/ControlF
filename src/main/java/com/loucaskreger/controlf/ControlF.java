package com.loucaskreger.controlf;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;

import com.loucaskreger.controlf.capability.ExtraSlotContainerCapability;
import com.loucaskreger.controlf.capability.IExtraSlotContainer;
import com.loucaskreger.controlf.client.gui.screen.InventoryScreen;
import com.loucaskreger.controlf.config.Config;
import com.loucaskreger.controlf.init.ContainerTypesInit;
import com.loucaskreger.controlf.networking.Networking;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ControlF.MOD_ID)
public class ControlF {

	public static final String MOD_ID = "controlf";
	public static final Logger LOGGER = LogManager.getLogger();

	public ControlF() {
		final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setupCommon);
		bus.addListener(this::setupClient);
		ContainerTypesInit.CONTAINER_TYPES.register(bus);
		Networking.registerMessages();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

	}

	private void setupCommon(final FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(IExtraSlotContainer.class, new Capability.IStorage<IExtraSlotContainer>() {

			@Override
			public INBT writeNBT(Capability<IExtraSlotContainer> capability, IExtraSlotContainer instance,
					Direction side) {
				return null;
			}

			@Override
			public void readNBT(Capability<IExtraSlotContainer> capability, IExtraSlotContainer instance,
					Direction side, INBT nbt) {
				// TODO Auto-generated method stub

			}
		}, ExtraSlotContainerCapability::new);

		ScreenManager.registerFactory(ContainerTypesInit.SLOT_CONTAINER.get(), InventoryScreen::new);
	}

	private void setupClient(final FMLClientSetupEvent event) {
//		ClientRegistry.registerKeyBinding(EventSubscriber.press);
		ClientRegistry.registerKeyBinding(EventSubscriber.search);
	}

}
