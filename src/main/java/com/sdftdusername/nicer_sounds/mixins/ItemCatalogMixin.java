package com.sdftdusername.nicer_sounds.mixins;

import com.sdftdusername.nicer_sounds.Sounds;
import finalforeach.cosmicreach.audio.SoundManager;
import finalforeach.cosmicreach.items.ItemCatalog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCatalog.class)
public class ItemCatalogMixin {
    @Shadow
    private boolean shown = false;

    @Inject(method = "show", at = @At("HEAD"))
    public void show(CallbackInfo ci) {
        if (!shown)
            SoundManager.INSTANCE.playSound(Sounds.OpenMenuSound);
    }

    @Inject(method = "hide", at = @At("HEAD"))
    public void hide(CallbackInfo ci) {
        if(shown)
            SoundManager.INSTANCE.playSound(Sounds.CloseMenuSound);
    }
}
