package com.loucaskreger.hwylf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.loucaskreger.hwylf.client.EventSubscriber;
import com.loucaskreger.hwylf.config.Config;
import com.loucaskreger.hwylf.networking.Networking;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HWYLF.MOD_ID)
public class HWYLF {
	public static final String MOD_ID = "hwylf";
	public static final Logger LOGGER = LogManager.getLogger();

	public HWYLF() {
		final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setupCommon);
		bus.addListener(this::setupClient);
		Networking.registerMessages();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

	}

	private void setupCommon(final FMLCommonSetupEvent event) {
	}

	private void setupClient(final FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(EventSubscriber.search);
	}

}
