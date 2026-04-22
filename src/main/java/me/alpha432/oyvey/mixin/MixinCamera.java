package me.alpha432.oyvey.mixin;

import me.alpha432.oyvey.features.modules.render.Freecam;
import me.alpha432.oyvey.OyVey; // Или другой путь к твоему менеджеру модулей
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow protected abstract void setPos(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("RETURN"))
    private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        // Получаем твой модуль Freecam из менеджера модулей
        Freecam freecam = OyVey.moduleManager.getModuleByClass(Freecam.class);

        if (freecam != null && freecam.isEnabled()) {
            // Подменяем позицию и поворот камеры на данные из "духа"
            this.setPos(freecam.getCamPos().x, freecam.getCamPos().y, freecam.getCamPos().z);
            this.setRotation(freecam.getCamYaw(), freecam.getCamPitch());
        }
    }
}
