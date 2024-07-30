package com.sdftdusername.nicer_sounds.mixins;

import com.sdftdusername.nicer_sounds.Sounds;
import finalforeach.cosmicreach.blockevents.BlockEventTrigger;
import finalforeach.cosmicreach.blockevents.actions.BlockActionPlaySound2D;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockActionPlaySound2D.class)
public class BlockActionPlaySound2DMixin {
    @Shadow
    String sound;

    @Inject(method = "act(Lfinalforeach/cosmicreach/blocks/BlockState;Lfinalforeach/cosmicreach/blockevents/BlockEventTrigger;Lfinalforeach/cosmicreach/world/Zone;)V", at = @At("HEAD"), cancellable = true)
    public void act(BlockState srcBlockState, BlockEventTrigger blockEventTrigger, Zone zone, CallbackInfo ci) {
        boolean placeSnd = sound.equals("block-place.ogg");
        boolean breakSnd = sound.equals("block-break.ogg");

        if (placeSnd || breakSnd) {
            int index;
            if (placeSnd)
                index = 5;
            else
                index = 6;

            Block block = srcBlockState.getBlock();
            if (block != null) {
                String blockId = block.getStringId();
                if (Sounds.BlockRoutes.containsKey(blockId)) {
                    String blockSound = Sounds.BlockRoutes.get(blockId);
                    if (Sounds.SoundActions.containsKey(blockSound))
                        Sounds.PlaySound(Sounds.BlockRoutes.get(blockId), Sounds.SoundActions.get(blockSound)[index]);
                }
            }

            ci.cancel();
        }
    }
}
