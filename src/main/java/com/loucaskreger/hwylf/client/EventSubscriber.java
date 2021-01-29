package com.loucaskreger.hwylf.client;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import com.loucaskreger.hwylf.HWYLF;
import com.loucaskreger.hwylf.client.screen.widget.RichTextFieldWidget;
import com.loucaskreger.hwylf.config.Config;
import com.loucaskreger.hwylf.networking.Networking;
import com.loucaskreger.hwylf.networking.packet.ItemLocationRequestPacket;
import com.loucaskreger.hwylf.networking.packet.ResetRendersPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = HWYLF.MOD_ID)
public class EventSubscriber {

	public static final KeyBinding search = new KeyBinding(HWYLF.MOD_ID + ".key.search", KeyConflictContext.UNIVERSAL,
			KeyModifier.NONE, getInput(GLFW_KEY_V), HWYLF.MOD_ID + ".key.categories");

	private static InputMappings.Input getInput(int key) {
		return InputMappings.Type.KEYSYM.getOrMakeInput(key);
	}

	static final Minecraft mc = Minecraft.getInstance();

	public static RichTextFieldWidget tf;
	public static String fieldText = "";
	private static boolean skip;

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
		if (event.getConfig().getSpec() == Config.CLIENT_SPEC) {
			Config.bakeConfig();
		}
	}

	@SubscribeEvent
	public static void keyPressed(final GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
		if (search.getKey().getKeyCode() == event.getKeyCode()) {
			Screen currentScreen = mc.currentScreen;
			if (currentScreen != null) {

				if (currentScreen instanceof ContainerScreen) {

					ContainerScreen<?> cs = (ContainerScreen<?>) currentScreen;
					Slot slot = cs.getSlotUnderMouse();
					if (slot != null) {
						Networking.INSTANCE.sendToServer(new ItemLocationRequestPacket(slot.getStack()));
						event.setCanceled(true);
					}
				}
			}
		}
		if (tf != null) {
			if (!tf.isFocused() && mc.gameSettings.keyBindChat.getKey().getKeyCode() == event.getKeyCode()) {
				tf.setFocused2(true);
				skip = true;
			} else if (tf.isFocused()) {
				for (KeyBinding k : mc.gameSettings.keyBindings) {
					if (k.isActiveAndMatches(InputMappings.getInputByCode(event.getKeyCode(), event.getScanCode()))) {
						event.setCanceled(true);
						break;
					}
				}
			}
		}

	}

	@SubscribeEvent
	public static void drawScreen(final GuiScreenEvent.DrawScreenEvent.Post event) {

		Screen gui = event.getGui();
		if (gui instanceof InventoryScreen) {
			RecipeBookGui recipeBookScreen = ((InventoryScreen) gui).getRecipeGui();
			if (!recipeBookScreen.isVisible()) {
//				gui.setFocusedDefault(tf);
				gui.setListener(tf);
				tf.setVisible(true);
			} else {
				tf.setVisible(false);
			}

		}
	}

	@SubscribeEvent
	public static void clientTick(final ClientTickEvent event) {
		if (search.isPressed() && mc.currentScreen == null) {
			Networking.INSTANCE.sendToServer(new ResetRendersPacket());
		}

	}

	@SubscribeEvent
	public static void onGuiInit(final GuiScreenEvent.InitGuiEvent.Post event) {
		Screen gui = event.getGui();
		if (gui instanceof InventoryScreen && mc.playerController.gameIsSurvivalOrAdventure()) {
			ContainerScreen<?> containerGui = (ContainerScreen<?>) gui;
			RecipeBookGui recipeBookScreen = ((InventoryScreen) gui).getRecipeGui();

			mc.keyboardListener.enableRepeatEvents(true);
			FontRenderer fontRenderer = mc.fontRenderer;
			/*
			 * Features: Scrolling while hovering over the rich text field will move the
			 * cursor through the text in the direction the person scrolls Right clicking
			 * the text field will clear it.
			 */

			tf = new RichTextFieldWidget(fontRenderer,
					recipeBookScreen.isVisible() ? containerGui.getGuiLeft() + 88 - 77 : containerGui.getGuiLeft() + 88,
					containerGui.getGuiTop() + 6 - 11, 80, fontRenderer.FONT_HEIGHT + 1, null,
					new StringTextComponent(fieldText));

			event.addWidget(tf);

			tf.setInitialText(fieldText);
			tf.setMaxStringLength(50);
			tf.setEnableBackgroundDrawing(true);
			tf.setTextColor(16777215);
			tf.setCanLoseFocus(true);
			tf.setSelectOnFocus(true);
			tf.setVisible(true);

			gui.setListener(tf);
//			gui.setFocusedDefault(tf);
		} else {
			tf = null;
		}
	}

	@SubscribeEvent
	public static void onCharTyped(final GuiScreenEvent.KeyboardCharTypedEvent.Pre event) {
		if (tf != null) {
			if (skip) {
				skip = false;
				event.setCanceled(true);
			}
		}
	}

//	@SubscribeEvent
//	public void onKeyPressed(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
//		System.out.println("Here");
//		if (tf != null) {
//			if (!tf.isFocused() && mc.gameSettings.keyBindChat.getKey().getKeyCode() == event.getKeyCode()) {
//				tf.setFocused2(true);
//				skip = true;
//			} else if (tf.isFocused()) {
//				System.out.println("Here Again");
//				for (KeyBinding k : mc.gameSettings.keyBindings) {
//					if (k.isActiveAndMatches(InputMappings.getInputByCode(event.getKeyCode(), event.getScanCode()))) {
//						event.setCanceled(true);
//						break;
//					}
//				}
//			}
//		}
//	}

}
