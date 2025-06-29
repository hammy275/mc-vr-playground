package com.hammy275.mcvrplayground.shared;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;

// Modified version of Mojang's ThrownItemRenderer
public class ScaledItemRenderer<T extends Entity & ScaledItemSupplier> extends EntityRenderer<T, ScaledItemRendererState> {

    private final ItemModelResolver itemModelResolver;

    public ScaledItemRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public ScaledItemRendererState createRenderState() {
        return new ScaledItemRendererState();
    }

    @Override
    public void extractRenderState(T entity, ScaledItemRendererState state, float f) {
        super.extractRenderState(entity, state, f);
        state.scale = entity.getScale();
        state.roll = entity.getRoll();
        this.itemModelResolver.updateForNonLiving(state.item, entity.getItem(), ItemDisplayContext.GROUND, entity);
    }

    @Override
    public void render(ScaledItemRendererState state, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        poseStack.scale(state.scale, state.scale, state.scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        // Rotate by roll
        poseStack.mulPose(Axis.ZN.rotation(state.roll));
        state.item.render(poseStack, multiBufferSource, i, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(state, poseStack, multiBufferSource, i);
    }
}
