package me.alpha432.oyvey.mixin;

import me.alpha432.oyvey.features.modules.player.AntiBlind;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {
    @Inject(method = "getDarknessGamma", at = @At("HEAD"), cancellable = true)
    private void onGetDarknessGamma(float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (AntiBlind.getInstance().isEnabled()) {
            cir.setReturnValue(0.0f); // Убираем пульсацию тьмы
        }
    }
}
