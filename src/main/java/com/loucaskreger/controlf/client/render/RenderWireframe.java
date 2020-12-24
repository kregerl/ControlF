package com.loucaskreger.controlf.client.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.loucaskreger.controlf.ControlF;
import com.loucaskreger.controlf.config.ClientConfig;
import com.loucaskreger.controlf.networking.Networking;
import com.loucaskreger.controlf.networking.packet.CheckInventoryRequestPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ControlF.MOD_ID, value = Dist.CLIENT)
public class RenderWireframe {
	private static final Logger LOGGER = LogManager.getLogger();

	public static ConcurrentHashMap<BlockPos, ItemStack> inventories = new ConcurrentHashMap<BlockPos, ItemStack>();

	private static BlockPos bPos = null;

	@SubscribeEvent
	public static void rightClick(final PlayerInteractEvent.RightClickBlock event) {
		BlockPos pos = event.getPos();
		if (inventories.containsKey(pos)) {
			bPos = pos;
		}
	}

	@SubscribeEvent
	public static void onContainerClose(final PlayerContainerEvent.Close event) {

		if (bPos != null && inventories.get(bPos) != null) {
			Networking.INSTANCE.sendToServer(new CheckInventoryRequestPacket(bPos, inventories.get(bPos)));
		}

	}

	@SubscribeEvent
	public static void render(final RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getInstance();

		World world = mc.world;
		IRenderTypeBuffer buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		IVertexBuilder builder = buffer.getBuffer(RenderHelper.NO_DEPTH_LINE);
		MatrixStack matrixStack = event.getMatrixStack();

		Vec3d projectedView = mc.gameRenderer.getActiveRenderInfo().getProjectedView();

		Iterator<BlockPos> it = inventories.keySet().iterator();
		while (it.hasNext()) {
			BlockPos pos = it.next();
			if (world.getTileEntity(pos) == null) {
				it.remove();
			} else {
				shapeToWireframe(pos, world, matrixStack, builder, projectedView);

			}
		}

		RenderSystem.disableDepthTest();

		((Impl) buffer).finish();

	}

	private static void shapeToWireframe(BlockPos pos, World world, MatrixStack matrixStack, IVertexBuilder builder,
			Vec3d projectedView) {
		BlockState state = world.getBlockState(pos);
		VoxelShape shape = state.getShape(world, pos);
		List<AxisAlignedBB> boundingBoxes = shape.toBoundingBoxList();
		List<Vec3d[][]> vecs = populateArrays(boundingBoxes);

		matrixStack.push();

		matrixStack.translate(-projectedView.x + pos.getX(), -projectedView.y + pos.getY(),
				-projectedView.z + pos.getZ());

		Matrix4f matrix = matrixStack.getLast().getMatrix();
		for (Vec3d[][] vecArray : vecs) {
			for (Vec3d[] vec : vecArray) {
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

	private static List<Vec3d[][]> populateArrays(List<AxisAlignedBB> boundingBoxes) {
		List<Vec3d[][]> vecs = new ArrayList<Vec3d[][]>();
		for (AxisAlignedBB boundingBox : boundingBoxes) {
			Vec3d[] top = new Vec3d[4];
			Vec3d[] bottom = new Vec3d[4];
			bottom[0] = new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
			bottom[1] = new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
			bottom[2] = new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
			bottom[3] = new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);

			top[0] = new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
			top[1] = new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
			top[2] = new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
			top[3] = new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
			vecs.add(new Vec3d[][] { top, bottom });
		}

		return vecs;

	}

	private static void drawLine(Matrix4f matrixPos, IVertexBuilder renderBuffer, Vec3d startVertex, Vec3d endVertex) {

		renderBuffer.pos(matrixPos, (float) startVertex.getX(), (float) startVertex.getY(), (float) startVertex.getZ())
				.color(ClientConfig.red.get(), ClientConfig.green.get(), ClientConfig.blue.get(), 255).endVertex();

		renderBuffer.pos(matrixPos, (float) endVertex.getX(), (float) endVertex.getY(), (float) endVertex.getZ())
				.color(ClientConfig.red.get(), ClientConfig.green.get(), ClientConfig.blue.get(), 255).endVertex();
	}

}