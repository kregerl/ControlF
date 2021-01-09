package com.loucaskreger.controlf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.loucaskreger.controlf.config.Config;
import com.loucaskreger.controlf.networking.Networking;
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
		Networking.registerMessages();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

//		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> {
//			return new ModSettingsScreen();
//		});

	}

	private void setupCommon(final FMLCommonSetupEvent event) {

	}

	private void setupClient(final FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(EventSubscriber.search);
	}

}
