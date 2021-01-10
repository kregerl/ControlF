package com.loucaskreger.hwylf.client.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.loucaskreger.hwylf.HWYLF;
import com.loucaskreger.hwylf.client.EventSubscriber;
import com.loucaskreger.hwylf.config.ClientConfig;
import com.loucaskreger.hwylf.networking.Networking;
import com.loucaskreger.hwylf.networking.packet.CheckInventoryRequestPacket;
import com.loucaskreger.hwylf.networking.packet.CheckSearchInventoryRequestPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HWYLF.MOD_ID, value = Dist.CLIENT)
public class RenderWireframe {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ResourceLocation TEXTURE = new ResourceLocation(HWYLF.MOD_ID, "textures/gui/extrapart.png");
//	private static final ResourceLocation SEARCH = new ResourceLocation(ControlF.MOD_ID, "textures/gui/search.png");

	public static ConcurrentHashMap<BlockPos, ItemStack> inventoryPos = new ConcurrentHashMap<BlockPos, ItemStack>();
	public static ConcurrentHashMap<BlockPos, ItemStack> searchPos = new ConcurrentHashMap<BlockPos, ItemStack>();

	public static List<Item> itemValues = new ArrayList<Item>();
	public static List<Item> searchItemValues = new ArrayList<Item>();

	public static BlockPos bPos = null;
	public static boolean force = false;

	static Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public static void leftClick(final PlayerInteractEvent.LeftClickBlock event) {
		BlockPos pos = event.getPos();
		if (inventoryPos.containsKey(pos)) {
			Networking.INSTANCE.sendToServer(new CheckInventoryRequestPacket(pos, inventoryPos.get(pos)));
			return;
		}
		if (searchPos.containsKey(pos)) {
			Networking.INSTANCE.sendToServer(new CheckSearchInventoryRequestPacket(bPos, searchPos.get(bPos)));
			return;
		}
	}

	@SubscribeEvent
	public static void rightClick(final PlayerInteractEvent.RightClickBlock event) {
		BlockPos pos = event.getPos();
		if (inventoryPos.containsKey(pos) || searchPos.containsKey(pos)) {
			bPos = pos;
		}
	}

	@SubscribeEvent
	public static void onContainerClose(final PlayerContainerEvent.Close event) {
		if (event.getContainer() instanceof PlayerContainer) {
			String tfText = EventSubscriber.tf.getText();
			EventSubscriber.fieldText = tfText;
		}

		if (bPos != null) {
			// Getting null pointer exceptions here.
			if (!inventoryPos.isEmpty() && inventoryPos.containsKey(bPos)) {
				Networking.INSTANCE.sendToServer(new CheckInventoryRequestPacket(bPos, inventoryPos.get(bPos)));
			}
			if (!searchPos.isEmpty() && searchPos.containsKey(bPos)) {
				Networking.INSTANCE.sendToServer(new CheckSearchInventoryRequestPacket(bPos, searchPos.get(bPos)));
			}
		}

	}

	@SubscribeEvent
	public static void onForeground(final GuiContainerEvent.DrawForeground event) {
		if (bPos != null || force) {
			for (Slot s : event.getGuiContainer().getContainer().inventorySlots) {
				if (!(event.getGuiContainer().getContainer() instanceof PlayerContainer)) {
					ItemStack stack = s.getStack();
					if (!itemValues.contains(stack.getItem()) && !searchItemValues.contains(stack.getItem())) {
						int x = s.xPos;
						int y = s.yPos;
						RenderSystem.disableDepthTest();
						// ------------------------------------0xAARRGGBB
						AbstractGui.fill(event.getMatrixStack(), x, y, x + 16, y + 16, 0x90000000);
						RenderSystem.enableDepthTest();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBackground(final GuiContainerEvent.DrawBackground event) {
		if (event.getGuiContainer() instanceof InventoryScreen && mc.playerController.gameIsSurvivalOrAdventure()) {
			ContainerScreen<?> gui = event.getGuiContainer();
			RecipeBookGui recipeBookScreen = ((InventoryScreen) gui).getRecipeGui();
			int guiLeft = gui.getGuiLeft();
			int height = gui.height;

			TextureManager tm = mc.textureManager;

			if (!recipeBookScreen.isVisible()) {
				tm.bindTexture(TEXTURE);
				event.getGuiContainer().blit(event.getMatrixStack(), guiLeft + 75, height / 2 - 94, 0, 0, 101, 14);
			}
//			tm.bindTexture(SEARCH);
//			event.getGuiContainer().blit(guiLeft + 87, height / 2 - 89, 0, 0, 80, 10);
		}
	}

	@SubscribeEvent
	public static void render(final RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getInstance();

		World world = mc.world;
		IRenderTypeBuffer buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		IVertexBuilder builder = buffer.getBuffer(RenderHelper.NO_DEPTH_LINE);
		MatrixStack matrixStack = event.getMatrixStack();

		Vector3d projectedView = mc.gameRenderer.getActiveRenderInfo().getProjectedView();

		Iterator<BlockPos> inventoryIterator = inventoryPos.keySet().iterator();
		Iterator<BlockPos> searchIterator = searchPos.keySet().iterator();
		drawWireframe(inventoryIterator, world, matrixStack, builder, projectedView);
		drawWireframe(searchIterator, world, matrixStack, builder, projectedView);

		RenderSystem.disableDepthTest();

		((Impl) buffer).finish();

	}

	private static void drawWireframe(Iterator<BlockPos> iterator, World world, MatrixStack matrixStack,
			IVertexBuilder builder, Vector3d projectedView) {
		while (iterator.hasNext()) {
			BlockPos pos = iterator.next();
			if (world.getTileEntity(pos) == null) {
				iterator.remove();
			} else {
				shapeToWireframe(pos, world, matrixStack, builder, projectedView);

			}
		}
	}

	private static void shapeToWireframe(BlockPos pos, World world, MatrixStack matrixStack, IVertexBuilder builder,
			Vector3d projectedView) {
		BlockState state = world.getBlockState(pos);
		VoxelShape shape = state.getShape(world, pos);
		List<AxisAlignedBB> boundingBoxes = shape.toBoundingBoxList();
		List<Vector3d[][]> vecs = populateArrays(boundingBoxes);

		matrixStack.push();

		matrixStack.translate(-projectedView.x + pos.getX(), -projectedView.y + pos.getY(),
				-projectedView.z + pos.getZ());

		Matrix4f matrix = matrixStack.getLast().getMatrix();
		for (Vector3d[][] vecArray : vecs) {
			for (Vector3d[] vec : vecArray) {
				drawLine(matrix, builder, vec[0], vec[1]);
				drawLine(matrix, builder, vec[1], vec[2]);
				drawLine(matrix, builder, vec[2], vec[3]);
				drawLine(matrix, builder, vec[3], vec[0]);

			}

			drawLine(matrix, builder, vecArray[0][0], vecArray[1][0]);
			drawLine(matrix, builder, vecArray[0][1], vecArray[1][1]);
			drawLine(matrix, builder, vecArray[0][2], vecArray[1][2]);
			drawLine(matrix, builder, vecArray[0][3], vecArray[1][3]);
		}

		matrixStack.pop();

	}

	private static List<Vector3d[][]> populateArrays(List<AxisAlignedBB> boundingBoxes) {
		List<Vector3d[][]> vecs = new ArrayList<Vector3d[][]>();
		for (AxisAlignedBB boundingBox : boundingBoxes) {
			Vector3d[] top = new Vector3d[4];
			Vector3d[] bottom = new Vector3d[4];
			bottom[0] = new Vector3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
			bottom[1] = new Vector3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
			bottom[2] = new Vector3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
			bottom[3] = new Vector3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);

			top[0] = new Vector3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
			top[1] = new Vector3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
			top[2] = new Vector3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
			top[3] = new Vector3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
			vecs.add(new Vector3d[][] { top, bottom });
		}

		return vecs;

	}

	private static void drawLine(Matrix4f matrixPos, IVertexBuilder renderBuffer, Vector3d startVertex,
			Vector3d endVertex) {

		renderBuffer.pos(matrixPos, (float) startVertex.getX(), (float) startVertex.getY(), (float) startVertex.getZ())
				.color(ClientConfig.red.get(), ClientConfig.green.get(), ClientConfig.blue.get(), 255).endVertex();

		renderBuffer.pos(matrixPos, (float) endVertex.getX(), (float) endVertex.getY(), (float) endVertex.getZ())
				.color(ClientConfig.red.get(), ClientConfig.green.get(), ClientConfig.blue.get(), 255).endVertex();
	}

}