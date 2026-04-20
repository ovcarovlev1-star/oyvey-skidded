package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright extends Module {
    
    // Режим работы: Гамма или Эффект
    public enum Mode {
        GAMMA, EFFECT
    }
    
    // В OyVey настройки обычно объявляются так (адаптируй под свой класс Setting)
    public Mode mode = Mode.EFFECT;

    public FullBright() {
        super("FullBright", "Makes everything bright as day", Category.RENDER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (mode == Mode.EFFECT) {
            // Накладываем бесконечный эффект ночного зрения без частиц
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1000, 0, false, false));
        } else {
            // Способ через Гамму (может не работать на некоторых серверах/версиях)
            mc.options.getGamma().setValue(100.0);
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null) return;

        if (mode == Mode.EFFECT) {
            // Убираем эффект при выключении модуля
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        } else {
            // Возвращаем стандартную яркость
            mc.options.getGamma().setValue(1.0);
        }
    }
}
