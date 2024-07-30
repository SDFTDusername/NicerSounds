package com.sdftdusername.nicer_sounds.mixins;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.sdftdusername.nicer_sounds.Sounds;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.entities.Player;
import finalforeach.cosmicreach.entities.PlayerController;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerController.class)
public class PlayerControllerMixin {
    @Shadow
    private Vector3 lastVelocity = new Vector3();

    @Shadow
    Player player;

    @Unique
    public double limit = 50.0d;

    @Unique
    public double waterLimit = 65.0d;

    @Unique
    public double moved = limit;

    @Unique
    public boolean previouslyGrounded = true;

    @Unique
    public boolean previouslyInWater = false;

    @Inject(method = "updateMovement", at = @At("TAIL"))
    private void updateMovement(Zone zone, CallbackInfo ci) {
        Entity entity = player.getEntity();

        if (entity.noClip)
            moved = limit;
        else {
            boolean inWater = getBlockIdAtPosition(zone, entity.position, new Vector3(0, 1, 0)).equals("base:water");
            boolean partiallyInWater = getBlockIdAtPosition(zone, entity.position, new Vector3(0, 0.1f, 0)).equals("base:water");

            if (previouslyInWater != inWater) {
                previouslyInWater = inWater;
                Sounds.PlaySound("water", "through");
            }

            boolean playLanding = false;
            if (previouslyGrounded != entity.isOnGround) {
                previouslyGrounded = entity.isOnGround;

                if (entity.isOnGround) {
                    moved = limit + 1.0d;
                    playLanding = true;
                }
            }

            Vector3 velocity = new Vector3(lastVelocity);
            velocity.y = 0.0f;

            if (entity.isOnGround || inWater)
                moved += velocity.len();

            double currentLimit = inWater ? waterLimit : limit;
            if (moved > currentLimit) {
                moved %= currentLimit;

                if (entity.isOnGround || inWater) {
                    String blockId = partiallyInWater ? "base:water" : getBlockIdAtPosition(zone, entity.position, new Vector3(0.0f, -0.5f, 0.0f));

                    if (!blockId.isEmpty()) {
                        if (Sounds.BlockRoutes.containsKey(blockId)) {
                            String blockSound = Sounds.BlockRoutes.get(blockId);
                            if (Sounds.SoundActions.containsKey(blockSound))
                                Sounds.PlaySound(Sounds.BlockRoutes.get(blockId), Sounds.SoundActions.get(blockSound)[getActionIndex(playLanding, entity)]);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private int getActionIndex(boolean playLanding, Entity entity) {
        if (playLanding)
            return 4;
        else if (player.isProne)
            return 3;
        else if (entity.isSneaking)
            return 2;
        else if (player.isSprinting)
            return 1;

        return 0;
    }

    @Unique
    private String getBlockIdAtPosition(Zone zone, Vector3 position, Vector3 offset) {
        Vector3 blockCheckPosition = new Vector3(position);
        blockCheckPosition.add(offset);

        blockCheckPosition.x = MathUtils.floor(blockCheckPosition.x);
        blockCheckPosition.y = MathUtils.floor(blockCheckPosition.y);
        blockCheckPosition.z = MathUtils.floor(blockCheckPosition.z);

        Chunk chunk = zone.getChunkAtPosition(blockCheckPosition);
        if (chunk != null) {
            int blockX = Math.floorMod((int)blockCheckPosition.x, 16);
            int blockY = Math.floorMod((int)blockCheckPosition.y, 16);
            int blockZ = Math.floorMod((int)blockCheckPosition.z, 16);

            BlockState blockState = chunk.getBlockState(blockX, blockY, blockZ);
            if (blockState != null) {
                Block block = blockState.getBlock();
                if (block != null) {
                    return block.getStringId();
                }
            }
        }

        return "";
    }
}
