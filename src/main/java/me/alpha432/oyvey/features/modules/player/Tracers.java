package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import java.awt.Color;

public class Tracers extends Module {
    private final Setting<Integer> range = register(new Setting<>("Range", 256, 10, 512));
    private final Setting<Boolean> players = register(new Setting<>("Players", true));
    private final Setting<Boolean> friends = register(new Setting<>("Friends", true));
    private final Setting<Boolean> mobs = register(new Setting<>("Mobs", false));
    private final Setting<Boolean> stem = register(new Setting<>("Stem", true));
    
    private final Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    private final Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    private final Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    private final Setting<Integer> alpha = register(new Setting<>("Alpha", 255, 0, 255));

    public Tracers() {
        // Устанавливаем категорию PLAYER по твоему требованию
        super("Tracers", "Draws lines to entities", Category.PLAYER, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player || entity.isRemoved()) continue;
            if (mc.player.distanceTo(entity) > range.getValue()) continue;

            if (shouldRender(entity)) {
                Color color = getEntityColor(entity);
                drawTracer(entity, event.getPartialTicks(), color);
            }
        }
    }

    private boolean shouldRender(Entity entity) {
        if (entity instanceof PlayerEntity) return players.getValue();
        if (EntityUtil.isHostileMob(entity) || EntityUtil.isPassiveMob(entity)) return mobs.getValue();
        return false;
    }

    private Color getEntityColor(Entity entity) {
        if (entity instanceof PlayerEntity && friends.getValue() && OyVey.friendManager.isFriend(entity.getName().getString())) {
            return new Color(0, 255, 255, alpha.getValue()); // Голубой для друзей
        }
        return new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
    }

    private void drawTracer(Entity entity, float partialTicks, Color color) {
        // Рассчитываем позицию цели с учетом интерполяции (чтобы линии не дергались)
        double targetX = entity.prevX + (entity.getX() - entity.prevX) * partialTicks;
        double targetY = entity.prevY + (entity.getY() - entity.prevY) * partialTicks;
        double targetZ = entity.prevZ + (entity.getZ() - entity.prevZ) * partialTicks;

        // Позиция камеры
        Vec3d cameraPos = mc.getEntityRenderDispatcher().camera.getPos();

        // Направление взгляда игрока (откуда пойдет линия)
        Vec3d startVec = new Vec3d(0, 0, 75) // Длина "выноса" линии от лица
                .rotateX(-(float) Math.toRadians(mc.player.getPitch()))
                .rotateY(-(float) Math.toRadians(mc.player.getYaw()))
                .add(cameraPos.x, cameraPos.y + mc.player.getEyeHeight(mc.player.getPose()), cameraPos.z);

        // Конечная точка (середина тела сущности)
        double height = entity.getBoundingBox().maxY - entity.getBoundingBox().minY;
        Vec3d endVec = new Vec3d(targetX, targetY + (height / 2), targetZ);

        // Рисуем основную линию
        RenderUtil.drawLine(
                (float)(startVec.x - cameraPos.x), (float)(startVec.y - cameraPos.y), (float)(startVec.z - cameraPos.z),
                (float)(endVec.x - cameraPos.x), (float)(endVec.y - cameraPos.y), (float)(endVec.z - cameraPos.z),
                color, 1.0f
        );

        // Если включен Stem, рисуем вертикальную палку сквозь игрока
        if (stem.getValue()) {
            RenderUtil.drawVerticalLine(targetX - cameraPos.x, targetY - cameraPos.y, targetZ - cameraPos.z, (float)height, color);
        }
    }
}
