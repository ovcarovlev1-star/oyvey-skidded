package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;

public class Freecam extends Module {
    // Настройки скорости
    public float speed = 1.0f;
    public boolean tracer = true;

    // Данные "духа"
    private Vec3d camPos;
    private float camYaw;
    private float camPitch;

    public Freecam() {
        super("Freecam", "Spectator mode without changing gamemode", Category.RENDER, true, false, false);
    }

    // Это заставит OyVey показывать координаты духа в меню/списке
    @Override
    public String getDisplayInfo() {
        if (camPos == null) return null;
        return String.format("%.1f, %.1f, %.1f (Speed: %.1f)", camPos.x, camPos.y, camPos.z, speed);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) return;
        // Копируем позицию игрока при включении
        camPos = mc.player.getEyePos();
        camYaw = mc.player.getYaw();
        camPitch = mc.player.getPitch();
        
        // ВАЖНО: Тебе нужно будет создать миксин на Camera, 
        // чтобы игра использовала camPos вместо позиции игрока.
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        // 1. Движение духа (WASD)
        double forward = mc.player.input.movementForward;
        double strafe = mc.player.input.movementSideways;
        
        float yawRad = camYaw * ((float)Math.PI / 180F);
        double f = Math.sin(yawRad);
        double g = Math.cos(yawRad);

        // Рассчитываем вектор движения относительно взгляда духа
        double moveX = (strafe * g - forward * f) * speed;
        double moveZ = (forward * g + strafe * f) * speed;
        double moveY = 0;

        // Вверх/Вниз (Прыжок/Шифт)
        if (mc.options.jumpKey.isPressed()) moveY += speed;
        if (mc.options.sneakKey.isPressed()) moveY -= speed;

        camPos = camPos.add(moveX, moveY, moveZ);
    }

    // 2. Ускорение через колесико (Нужно вызвать это событие из твоего MouseMixin или Main)
    public void onMouseScroll(double amount) {
        if (!isEnabled()) return;
        
        if (amount > 0) {
            speed += 0.1f;
        } else if (amount < 0) {
            speed = Math.max(0.1f, speed - 0.1f);
        }
    }

    @Override
    public void onRender3D(MatrixStack matrixStack) {
        if (camPos == null || !tracer) return;

        // Рисуем линию (Трейсер) от реального игрока к духу
        Vec3d playerPos = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
        
        // Вызывай свой метод отрисовки линий:
        // RenderUtil.drawLine(matrixStack, playerPos, camPos, Color.WHITE);
    }

    // Геттеры для MixinCamera
    public Vec3d getCamPos() { return camPos; }
    public float getCamYaw() { return camYaw; }
    public float getCamPitch() { return camPitch; }
}
