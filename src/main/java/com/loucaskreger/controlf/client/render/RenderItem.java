package com.loucaskreger.controlf.client.render;

import com.loucaskreger.controlf.ControlF;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ControlF.MOD_ID, value = Dist.CLIENT)
public class RenderItem {

	@SubscribeEvent
	public static void render(final RenderWorldLastEvent event) {
//		Minecraft mc = Minecraft.getInstance();
//		ItemRenderer itemRenderer = mc.getItemRenderer();
//		Vec3d projectedView = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
//
//		ItemStack stack = new ItemStack(Items.DIAMOND, 1);
//		MatrixStack matrixStack = new MatrixStack();
//		matrixStack.push();
//		matrixStack.translate(-projectedView.x + 0, -projectedView.y + 100, -projectedView.z + 0);
//		matrixStack.scale(0.5f, 0.5f, 0.5f);
////			matrixStack.translate(0, 100, 0);
//
//		IRenderTypeBuffer buffer = /* IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer()) */ mc
//				.getRenderTypeBuffers().getBufferSource();
//		
//////		mc.getRenderManager().renderEntityStatic(eItem, -projectedView.x, -projectedView.y + 100, -projectedView.z,
//////				1.0f, event.getPartialTicks(), matrixStack, buffer, 10000);
////		itemRenderer.renderItem(stack, TransformType.NONE, 1000, OverlayTexture.NO_OVERLAY, matrixStack,
////				buffer);
//		matrixStack.pop();
	}
}
