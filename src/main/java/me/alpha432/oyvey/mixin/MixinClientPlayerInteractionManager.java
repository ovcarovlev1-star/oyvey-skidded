package me.alpha432.oyvey.mixin;

import me.alpha432.oyvey.features.modules.player.BreakDelay;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow 
    private int blockHitDelay;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // Проверяем, включен ли модуль
        if (BreakDelay.getInstance().isEnabled()) {
            // Если текущая задержка больше, чем выставленная в модуле — ставим свою
            if (this.blockHitDelay > BreakDelay.getInstance().cooldown.getValue()) {
                this.blockHitDelay = BreakDelay.getInstance().cooldown.getValue();
            }
        }
    }
}
