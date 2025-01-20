package com.hammy275.mcvrplayground.client.render;

import com.hammy275.mcvrplayground.common.entity.ScaledItemSupplier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;

// Modified version of Mojang's ThrownItemRenderer
public class ScaledItemRenderer<T extends Entity & ScaledItemSupplier> extends EntityRenderer<T> {

    private final ItemRenderer itemRenderer;

    public ScaledItemRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(T projectile, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        float scale = projectile.getScale();
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        // Rotate by roll
        poseStack.mulPose(Axis.ZN.rotation(projectile.getRoll()));
        this.itemRenderer.renderStatic(projectile.getItem(), ItemDisplayContext.GROUND, i, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, projectile.level(), projectile.getId());
        poseStack.popPose();
        super.render(projectile, f, g, poseStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS; // Used for Vanilla's ThrownItemRenderer, so using that here, too
    }
}
