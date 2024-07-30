package com.sdftdusername.nicer_sounds.mixins;

import com.sdftdusername.nicer_sounds.Sounds;
import finalforeach.cosmicreach.audio.SoundManager;
import finalforeach.cosmicreach.items.Hotbar;
import finalforeach.cosmicreach.items.ISlotContainer;
import finalforeach.cosmicreach.items.ItemSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hotbar.class)
public class HotbarMixin {
    @Shadow
    protected int lastSelectedSlotNum = -1;

    @Shadow
    private ISlotContainer container;

    @Inject(method = "selectSlot", at = @At("HEAD"))
    public void selectSlot(int slotNum, CallbackInfo ci) {
        SoundManager.INSTANCE.playSound(Sounds.SwitchSlotSound);
    }
}
