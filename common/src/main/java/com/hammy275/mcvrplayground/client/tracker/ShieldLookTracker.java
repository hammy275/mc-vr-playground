package com.hammy275.mcvrplayground.client.tracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.client.ItemInUseTracker;
import org.vivecraft.api.client.Tracker;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPartData;
import org.vivecraft.api.data.VRPose;

import java.util.Optional;

public class ShieldLookTracker implements Tracker, ItemInUseTracker {
    @Override
    public boolean itemInUse(LocalPlayer localPlayer) {
        // Should continue using an item if the player has a shield they want to likely be blocking with
        Optional<InteractionHand> shieldHand = shieldToBlockWith(localPlayer);
        return localPlayer.isUsingItem() && shieldHand.isPresent() && localPlayer.getUsedItemHand() == shieldHand.get();
    }

    @Override
    public boolean isActive(LocalPlayer localPlayer) {
        // This tracker is only active if the player is holding a shield in at least one hand and some item isn't
        // already being used. itemInUse() runs whether the Tracker is active or not, so we don't need to be active
        // when the shield is already being used.
        if (localPlayer.isUsingItem()) {
            return false;
        }
        for (InteractionHand hand : InteractionHand.values()) {
            if (localPlayer.getItemInHand(hand).is(Items.SHIELD)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doProcess(LocalPlayer localPlayer) {
        // If the player has a shield they likely want to block with, start using that shield to block.
        Optional<InteractionHand> shieldHand = shieldToBlockWith(localPlayer);
        if (shieldHand.isPresent()) {
            Minecraft.getInstance().gameMode.useItem(localPlayer, shieldHand.get());
        }
    }

    @Override
    public TrackerTickType tickType() {
        // This Tracker only needs to run every game tick.
        return TrackerTickType.PER_TICK;
    }

    private Optional<InteractionHand> shieldToBlockWith(LocalPlayer localPlayer) {
        // Get pose of the VR player
        VRPose pose = VRClientAPI.instance().getPreTickWorldPose();
        // Get the direction the HMD is facing, which is the direction the player is looking
        VRBodyPartData hmdData = pose.getHMD();
        Vec3 hmdRot = hmdData.getDir();
        // For each hand holding a shield
        for (InteractionHand hand : InteractionHand.values()) {
            if (localPlayer.getItemInHand(hand).is(Items.SHIELD)) {
                // Get the direction that hand is facing
                VRBodyPartData handData = pose.getHand(hand);
                Vec3 handRot = handData.getDir();
                // Get the difference in angles between the direction the player is looking (the HMD) and the direction
                // the hand with the shield is pointing.
                double angle = Math.acos(hmdRot.dot(handRot));
                // If the difference is small, then the user likely wants to block a mob with that hand.
                if (angle < Math.PI / 6) {
                    return Optional.of(hand);
                }
            }
        }
        // No hand has a shield and is facing a similar direction to the HMD.
        return Optional.empty();
    }
}
