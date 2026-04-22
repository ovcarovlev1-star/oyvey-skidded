package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class BreakDelay extends Module {
    // Настройка задержки (0 - ломает без пауз)
    public final Setting<Integer> cooldown = register(new Setting<>("Cooldown", 0, 0, 5));

    private static BreakDelay INSTANCE;

    public BreakDelay() {
        super("BreakDelay", "Changes the delay between breaking blocks", Category.PLAYER, true, false, false);
        INSTANCE = this;
    }

    public static BreakDelay getInstance() {
        if (INSTANCE == null) INSTANCE = new BreakDelay();
        return INSTANCE;
    }
}
