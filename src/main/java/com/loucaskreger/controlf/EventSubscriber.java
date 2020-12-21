package com.loucaskreger.controlf;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;

import com.loucaskreger.controlf.config.ClientConfig;
import com.loucaskreger.controlf.config.Config;
import com.loucaskreger.controlf.networking.Networking;
import com.loucaskreger.controlf.networking.packet.ItemLocationRequestPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = ControlF.MOD_ID)
public class EventSubscriber {

//	public static final KeyBinding press = new KeyBinding(ControlF.MOD_ID + ".key.verticalscroll",
//			KeyConflictContext.IN_GAME, KeyModifier.CONTROL, getInput(GLFW_KEY_F), ControlF.MOD_ID + ".key.categories");

	public static final KeyBinding search = new KeyBinding(ControlF.MOD_ID + ".key.search", KeyConflictContext.GUI,
			KeyModifier.CONTROL, getInput(GLFW_KEY_F), ControlF.MOD_ID + ".key.categories");

	private static InputMappings.Input getInput(int key) {
		return InputMappings.Type.KEYSYM.getOrMakeInput(key);
	}

	static ResourceLocation location = new ResourceLocation(ControlF.MOD_ID, "extraslot");

	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
//		Entity entity = event.getObject();
//		if (entity instanceof PlayerEntity) {
//			event.addCapability(new ResourceLocation(ControlF.MOD_ID, "extracontainer"),
//					new ExtraSlotContainerProvider((PlayerEntity) entity));
//		}
	}

	static final Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == Config.CLIENT_SPEC) {
			Config.bakeConfig();

		}
	}

	@SubscribeEvent
	public static void clientTick(final ClientTickEvent event) {
		if (search.isPressed()) {
			Screen currentScreen = mc.currentScreen;
			if (currentScreen instanceof ContainerScreen) {

				currentScreen = (ContainerScreen<?>) currentScreen;
				Slot slot = ((ContainerScreen<?>) currentScreen).getSlotUnderMouse();
				Networking.INSTANCE.sendToServer(new ItemLocationRequestPacket(slot.getStack()));
			}
		}

	}

}
