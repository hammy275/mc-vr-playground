package com.hammy275.mcvrplayground.shared;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ScaledItemRendererState extends EntityRenderState {

    public float scale = 1f;
    public float roll = 0f;
    public ItemStackRenderState item = new ItemStackRenderState();

    public ScaledItemRendererState() {}

}
