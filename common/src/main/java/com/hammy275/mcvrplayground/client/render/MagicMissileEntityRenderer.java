package com.hammy275.mcvrplayground.client.render;

import com.hammy275.mcvrplayground.common.entity.MagicMissileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

// Modified version of Mojang's ThrownItemRenderer
public class MagicMissileEntityRenderer extends EntityRenderer<MagicMissileEntity> {

    private static final float SCALE = 1.5f;
    private final ItemRenderer itemRenderer;
    public MagicMissileEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(MagicMissileEntity missile, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        poseStack.scale(SCALE, SCALE, SCALE);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        // Rotate by controller roll
        poseStack.mulPose(Axis.ZN.rotation(missile.getEntityData().get(MagicMissileEntity.ROLL)));
        this.itemRenderer.renderStatic(missile.getItem(), ItemDisplayContext.GROUND, i, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, missile.level(), missile.getId());
        poseStack.popPose();
        super.render(missile, f, g, poseStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(MagicMissileEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS; // Used for Vanilla's ThrownItemRenderer, so using that here, too
    }
}
