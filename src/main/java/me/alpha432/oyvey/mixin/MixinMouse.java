package me.alpha432.oyvey.mixin;

import me.alpha432.oyvey.features.modules.player.FreeLook;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouse {

    @Inject(
        method = "updateMouse", 
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"), 
        cancellable = true
    )
    private void onUpdateMouse(CallbackInfo ci) {
        FreeLook freeLook = FreeLook.getInstance();
        
        // Если модуль включен и мы в режиме свободной камеры
        if (freeLook != null && freeLook.cameraMode()) {
            // Используем наш аксессор, чтобы получить движение мыши
            double dx = ((MouseAccessor) this).getCursorDeltaX();
            double dy = ((MouseAccessor) this).getCursorDeltaY();
            
            // Отправляем данные в модуль для вращения камеры
            freeLook.handleMouseInput(dx, dy);
            
            // Отменяем выполнение оригинального метода, 
            // чтобы персонаж (ClientPlayerEntity) не вращался
            ci.cancel();
        }
    }
}
