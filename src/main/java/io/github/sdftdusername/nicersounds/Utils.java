package io.github.sdftdusername.nicersounds;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;

public class Utils {
    public static final Vector3[] OFFSETS = new Vector3[]{
            new Vector3(0.3f, 0, 0),
            new Vector3(-0.3f, 0, 0),
            new Vector3(0, 0, 0.3f),
            new Vector3(0, 0, -0.3f),
            new Vector3(0.3f, 0, 0.3f),
            new Vector3(0.3f, 0, -0.3f),
            new Vector3(-0.3f, 0, 0.3f),
            new Vector3(-0.3f, 0, -0.3f)
    };

    public enum MovementAction {
        PRESSED,
        HELD,
        RELEASED
    }

    public static BlockState GetBlockStateAtPosition(Zone zone, Vector3 position, Vector3 offset, boolean recursive) {
        Vector3 blockPosition = new Vector3(position);
        blockPosition.add(offset);

        blockPosition.x = MathUtils.floor(blockPosition.x);
        blockPosition.y = MathUtils.floor(blockPosition.y);
        blockPosition.z = MathUtils.floor(blockPosition.z);

        Chunk chunk = zone.getChunkAtPosition(blockPosition);
        if (chunk != null) {
            int blockX = Math.floorMod((int)blockPosition.x, 16);
            int blockY = Math.floorMod((int)blockPosition.y, 16);
            int blockZ = Math.floorMod((int)blockPosition.z, 16);

            BlockState blockState = chunk.getBlockState(blockX, blockY, blockZ);

            if (blockState.getBlock().equals(Block.AIR) && recursive) {
                for (Vector3 offset2 : OFFSETS) {
                    BlockState blockState2 = GetBlockStateAtPosition(zone, position, new Vector3(
                            offset.x + offset2.x,
                            offset.y + offset2.y,
                            offset.z + offset2.z
                    ), false);

                    if (blockState2 != null && !blockState2.getBlock().equals(Block.AIR))
                        return blockState2;
                }
            }

            return blockState;
        }

        return null;
    }

    public static BlockState GetBlockStateAtPosition(Zone zone, Vector3 position, Vector3 offset) {
        return GetBlockStateAtPosition(zone, position, offset, true);
    }
}
