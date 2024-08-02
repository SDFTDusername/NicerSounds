package com.sdftdusername.nicer_sounds.mixins;

import com.badlogic.gdx.math.Vector3;
import com.sdftdusername.nicer_sounds.Sounds;
import com.sdftdusername.nicer_sounds.Utils;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.entities.Player;
import finalforeach.cosmicreach.entities.PlayerController;
import finalforeach.cosmicreach.settings.Controls;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerController.class)
public abstract class PlayerControllerMixin {
    @Shadow private transient Vector3 tmpMovement;
    @Shadow public abstract Entity getEntity();
    @Shadow Player player;

    @Unique private boolean previouslyMoving = false;
    @Unique private boolean previouslyOnGround = true;
    @Unique private boolean previouslyInWater = false;

    @Unique private static final double limit = 25;
    @Unique private static final double waterLimit = 35;

    @Unique private double moved = 0;

    @Unique
    private void PlaySound(Zone zone, String action, boolean inWater) {
        Entity entity = getEntity();
        String blockId;

        if (inWater) {
            blockId = "base:water";
        } else {
            BlockState blockState = Utils.GetBlockStateAtPosition(zone, entity.position, new Vector3(0.0f, -0.5f, 0.0f));
            if (blockState == null)
                return;

            blockId = blockState.getBlock().getStringId();
        }

        if (!Sounds.BlockMaterials.containsKey(blockId)) // Check if the block has a material assigned to it
            return;

        String material = Sounds.BlockMaterials.get(blockId);

        if (!Sounds.SoundActions.containsKey(material)) // Check if the material has sounds
            return;

        Map<String, String> types = Sounds.SoundActions.get(material);

        if (!types.containsKey(action))
            return;
        String type = types.get(action);

        Sounds.PlaySound3D(material, type, entity.position);
    }

    @Unique
    private void PlaySound(Zone zone, String action) {
        PlaySound(zone, action, false);
    }

    @Unique
    private void PlayerJumped(Zone zone) {
        PlaySound(zone, "jump");
        moved = 0;
    }

    @Unique
    private void PlayerMovement(Zone zone, Vector3 velocity, Utils.MovementAction action) {
        Entity entity = getEntity();

        boolean partiallyInWater = entity.isInFluid();
        double currentLimit = partiallyInWater ? waterLimit : limit;

        switch (action) {
            case PRESSED:
                moved = currentLimit; // Immediately play a footstep sound
                break;
            case RELEASED:
                return;
        }

        if (entity.isOnGround || partiallyInWater)
            moved += velocity.len();

        if (moved > currentLimit) {
            moved %= currentLimit;

            if (entity.isOnGround || partiallyInWater) {
                String soundAction;

                if (player.isProne)
                    soundAction = "prone";
                else if (entity.isSneaking)
                    soundAction = "crouch";
                else if (player.isSprinting)
                    soundAction = "run";
                else
                    soundAction = "walk";

                PlaySound(zone, soundAction, partiallyInWater);
            }
        }
    }

    @Unique
    private void PlayerLanded(Zone zone, boolean jumped) {
        if (!jumped)
            PlaySound(zone, "land");
        moved = 0;
    }

    @Inject(method = "updateMovement", at = @At(value = "INVOKE", target = "Lcom/badlogic/gdx/math/Vector3;add(FFF)Lcom/badlogic/gdx/math/Vector3;", ordinal = 3))
    private void jumped(Zone zone, CallbackInfo ci) {
        PlayerJumped(zone);
    }

    @Inject(method = "updateMovement", at = @At("TAIL"))
    private void swim(Zone zone, CallbackInfo ci) {
        Entity entity = getEntity();

        BlockState blockState = Utils.GetBlockStateAtPosition(zone, entity.position, new Vector3(0, 1, 0));
        boolean inWater = blockState != null && blockState.getBlock().getStringId().equals("base:water");

        if (previouslyInWater != inWater) {
            previouslyInWater = inWater;

            if (!Sounds.BlockMaterials.containsKey("base:water"))
                return;
            String material = Sounds.BlockMaterials.get("base:water");

            if (!Sounds.SoundActions.containsKey(material))
                return;
            Map<String, String> types = Sounds.SoundActions.get(material);

            if (!types.containsKey("swim"))
                return;
            String type = types.get("swim");

            Sounds.PlaySound3D(material, type, entity.position);
        }
    }

    @Inject(method = "updateMovement", at = @At("TAIL"))
    private void checkForMovement(Zone zone, CallbackInfo ci) {
        Entity entity = getEntity();

        Vector3 velocity = new Vector3(tmpMovement);
        velocity.y = 0;

        float pressed = Controls.forwardPressed() + Controls.backwardPressed() + Controls.leftPressed() + Controls.rightPressed();
        boolean isMoving = !entity.noClip && (pressed != 0);

        if (previouslyMoving != isMoving) {
            previouslyMoving = isMoving;
            PlayerMovement(zone, velocity, isMoving ? Utils.MovementAction.PRESSED : Utils.MovementAction.RELEASED);
        } else if (isMoving) {
            PlayerMovement(zone, velocity, Utils.MovementAction.HELD);
        }
    }

    @Inject(method = "updateMovement", at = @At(value = "FIELD", target = "Lfinalforeach/cosmicreach/entities/Entity;noClip:Z", ordinal = 4))
    private void checkIfLanded(Zone zone, CallbackInfo ci) {
        Entity entity = getEntity();

        if (entity.noClip) {
            previouslyOnGround = entity.isOnGround;
        } else if (previouslyOnGround != entity.isOnGround) {
            previouslyOnGround = entity.isOnGround;
            if (entity.isOnGround)
                PlayerLanded(zone, Controls.jumpPressed());
        }
    }
}
