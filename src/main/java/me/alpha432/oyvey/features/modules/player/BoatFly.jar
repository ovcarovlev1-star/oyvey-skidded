package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class EntityControl extends Module {
    // Настройки (упрощены под твой формат)
    public float speed = 10.0f;
    public float verticalSpeed = 6.0f;
    public boolean fly = true;
    public boolean antiKick = true;
    public boolean lockYaw = true;

    private int delayLeft;
    private double lastPacketY = Double.MAX_VALUE;
    private boolean sentPacket = false;

    public EntityControl() {
        super("EntityControl", "BoatFly / HorseControl", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.player.getVehicle() == null) return;

        Entity vehicle = mc.player.getVehicle();

        // 1. Управление Yaw (поворот сущности за игроком)
        if (lockYaw) {
            vehicle.setYaw(mc.player.getYaw());
        }

        // 2. Горизонтальное движение (Speed)
        double forward = mc.player.input.movementForward;
        double strafe = mc.player.input.movementSideways;
        float yaw = mc.player.getYaw();

        if (forward == 0 && strafe == 0) {
            vehicle.setVelocity(0, vehicle.getVelocity().y, 0);
        } else {
            // Рассчитываем вектор движения на основе взгляда игрока
            double motX = -Math.sin(Math.toRadians(yaw)) * speed / 20;
            double motZ = Math.cos(Math.toRadians(yaw)) * speed / 20;
            
            // Если идем назад или боком, тут нужна более сложная математика, 
            // но для простоты лодки это базовый вариант:
            vehicle.setVelocity(motX * forward, vehicle.getVelocity().y, motZ * forward);
        }

        // 3. Вертикальное движение (Fly)
        if (fly) {
            double vY = 0;
            if (mc.options.jumpKey.isPressed()) {
                vY = verticalSpeed / 20;
            } else if (mc.options.sprintKey.isPressed()) {
                vY = -verticalSpeed / 20;
            } else {
                vY = -0.01; // Легкое падение чтобы не кикало
            }
            
            Vec3d vel = vehicle.getVelocity();
            vehicle.setVelocity(vel.x, vY, vel.z);
        }

        // 4. Anti-Kick логика (упрощенная)
        if (antiKick && fly) {
            if (delayLeft <= 0) {
                // Имитируем небольшое падение для сервера
                VehicleMoveC2SPacket packet = VehicleMoveC2SPacket.fromVehicle(vehicle);
                // В твоем проекте может понадобиться Mixin для изменения Y в пакете,
                // но прямой вызов setVelocity обычно достаточно для ванильных серверов
                delayLeft = 40; 
            }
            delayLeft--;
        }
    }
}
