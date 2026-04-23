package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Random;

public class TriggerBot extends Module {
    // Настройки в стиле OyVey
    private final Setting<Double> range = register(new Setting<>("Range", 4.0, 1.0, 6.0));
    private final Setting<Integer> delay = register(new Setting<>("DelayMS", 100, 0, 1000));
    private final Setting<Integer> randomDelay = register(new Setting<>("RandomizeMS", 50, 0, 500));
    private final Setting<Boolean> playersOnly = register(new Setting<>("PlayersOnly", true));
    private final Setting<Boolean> attackWhileUsing = register(new Setting<>("AttackWhileUsing", false));

    private long lastAttackTime = 0;
    private final Random random = new Random();

    public TriggerBot() {
        // Устанавливаем категорию PLAYER, как ты и просил
        super("TriggerBot", "Attacks entities you look at", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        // Проверка: не открыто ли какое-то меню (инвентарь, сундук)
        if (mc.currentScreen != null) return;

        // Проверка: не едим ли мы или не закрыты ли щитом (если настройка выключена)
        if (!attackWhileUsing.getValue() && mc.player.isUsingItem()) return;

        // Рассчитываем текущую задержку с учетом рандомизации (как в Wurst)
        long currentDelay = delay.getValue() + (randomDelay.getValue() > 0 ? random.nextInt(randomDelay.getValue()) : 0);

        if (System.currentTimeMillis() - lastAttackTime < currentDelay) return;

        // Проверяем, наведен ли прицел на сущность
        HitResult hitResult = mc.crosshairTarget;

        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hitResult).getEntity();

            if (isValid(target)) {
                attack(target);
            }
        }
    }

    private boolean isValid(Entity entity) {
        // Дистанция
        if (mc.player.distanceTo(entity) > range.getValue()) return false;

        // Только живые существа
        if (!(entity instanceof LivingEntity)) return false;
        if (((LivingEntity) entity).getHealth() <= 0) return false;

        // Фильтр игроков
        if (playersOnly.getValue() && !(entity instanceof PlayerEntity)) return false;

        // Не бьем себя (на всякий случай)
        if (entity == mc.player) return false;

        return true;
    }

    private void attack(Entity target) {
        // Сама атака через InteractionManager
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
        
        lastAttackTime = System.currentTimeMillis();
    }
}
