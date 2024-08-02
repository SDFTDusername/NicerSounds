package com.sdftdusername.nicer_sounds.mixins;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.sdftdusername.nicer_sounds.Sounds;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.items.*;
import finalforeach.cosmicreach.ui.UI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(UI.class)
public class UIMixin {
    @Shadow
    private Viewport uiViewport;

    @Shadow
    public static ItemCatalog itemCatalog;

    @Shadow
    public static ItemSlotCursor itemCursor;

    @Shadow
    public static boolean mouseOverUI;

    /**
     * @author SDFTDusername
     * @reason Play sound when grabbing or removing item
     */
    @Overwrite
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean playPlace = false; // Added
        boolean playBreak = false; // Added

        Block placeBlock = null; // Added
        Block breakBlock = null; // Added

        if (itemCatalog.isShown()) {
            screenX -= this.uiViewport.getScreenWidth() / 2;
            screenY -= this.uiViewport.getScreenHeight() / 2;
            float sx = (float)screenX / (float)this.uiViewport.getScreenWidth() * this.uiViewport.getWorldWidth();
            float sy = (float)screenY / (float)this.uiViewport.getScreenHeight() * this.uiViewport.getWorldHeight();
            if (itemCatalog.isPointInBounds(sx, sy)) {
                ItemSlot cursorSlot = itemCursor.getSlot(0);
                if (cursorSlot.itemStack != null) {
                    if (cursorSlot.itemStack.getItem() instanceof ItemBlock) { // Added
                        playBreak = true; // Added
                        breakBlock = ((ItemBlock)cursorSlot.itemStack.getItem()).getBlockState().getBlock(); // Added
                    } // Added
                    cursorSlot.itemStack = null;
                }

                mouseOverUI = true;
                int slotStart = itemCatalog.pagesToItemSlotNum.get(itemCatalog.curPage);
                int slotEnd = itemCatalog.getNumSlots() - 1;
                if (itemCatalog.curPage + 1 < itemCatalog.pagesToItemSlotNum.size) {
                    slotEnd = itemCatalog.pagesToItemSlotNum.get(itemCatalog.curPage + 1) - 1;
                }

                for (int i = slotStart; i <= slotEnd; i++) {
                    ItemSlot slot = itemCatalog.getSlot(i);
                    ItemStack itemStack = slot.itemStack;
                    if (itemStack != null && itemCatalog.isHoveredOverSlot(slot, this.uiViewport, sx, sy)) {
                        InGame.getLocalPlayer().inventory.merge(itemStack.copy(), ItemMergeStrategy.ONLY_ONE_SLOT);
                        playBreak = false; // Added
                        playPlace = true; // Added
                        placeBlock = ((ItemBlock)itemStack.getItem()).getBlockState().getBlock(); // Added
                    }
                }
            }
        }

        if (playPlace || playBreak) { // Added
            String blockId = ""; // Added
            int index = -1; // Added
            if (playPlace && placeBlock != null) { // Added
                blockId = placeBlock.getStringId(); // Added
                index = 5; // Added
            } else if (playBreak && breakBlock != null) { // Added
                blockId = breakBlock.getStringId(); // Added
                index = 6; // Added
            } // Added
            if (!(blockId.isEmpty() || index == -1)) { // Added
                if (Sounds.BlockMaterials.containsKey(blockId)) { // Added
                    String blockSound = Sounds.BlockMaterials.get(blockId); // Added
                    if (Sounds.SoundActions.containsKey(blockSound)) // Added
                        Sounds.PlaySound(blockSound, Sounds.SoundActions.get(blockSound)[index]); // Added
                } // Added
            } // Added
        } // Added

        return false;
    }
}
