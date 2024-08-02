package com.sdftdusername.nicer_sounds.mixins;

import com.badlogic.gdx.math.Vector3;
import com.sdftdusername.nicer_sounds.Sounds;
import finalforeach.cosmicreach.blockevents.BlockEventTrigger;
import finalforeach.cosmicreach.blockevents.actions.BlockActionPlaySound2D;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(BlockActionPlaySound2D.class)
public abstract class BlockActionPlaySound2DMixin {
    @Shadow
    String sound;

    @Shadow public abstract void act(BlockState srcBlockState, BlockEventTrigger blockEventTrigger, Zone zone);

    /**
     * @author SDFTDusername
     * @reason Play block placing and breaking sound
     */
    @Overwrite
    public void act(BlockState srcBlockState, BlockEventTrigger blockEventTrigger, Zone zone, Map<String, Object> args) {
        boolean placeSnd = sound.equals("block-place.ogg");
        boolean breakSnd = sound.equals("block-break.ogg");

        if (placeSnd || breakSnd) {
            int index;
            if (placeSnd)
                index = 5;
            else
                index = 6;

            BlockPosition blockPosition = (BlockPosition)args.get("blockPos");

            Block block = srcBlockState.getBlock();
            if (block != null) {
                String blockId = block.getStringId();
                if (Sounds.BlockMaterials.containsKey(blockId)) {
                    String blockSound = Sounds.BlockMaterials.get(blockId);
                    if (Sounds.SoundActions.containsKey(blockSound)) {
                        Vector3 position = new Vector3(
                                blockPosition.getGlobalX(),
                                blockPosition.getGlobalY(),
                                blockPosition.getGlobalZ()
                        );
                        Sounds.PlaySound3D(blockSound, Sounds.SoundActions.get(blockSound)[index], position);
                    }
                }
            }

            return;
        }

        act(srcBlockState, blockEventTrigger, zone);
    }
}
