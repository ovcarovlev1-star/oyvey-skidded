package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;

public class Panic extends Module {
    public Panic() {
        // Указываем категорию PLAYER, как ты и просил
        super("Panic", "Disables all active modules instantly", Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        // Проверяем, что менеджер модулей доступен
        if (OyVey.moduleManager == null || OyVey.moduleManager.modules == null) {
            this.disable();
            return;
        }

        // Итерируемся по списку всех модулей чита
        OyVey.moduleManager.modules.forEach(module -> {
            // Если модуль включен и это не сам Panic (чтобы не было рекурсии)
            if (module.isEnabled() && !module.equals(this)) {
                module.disable();
            }
        });

        // После того как всё выключили, выключаем и сам Panic
        this.disable();
    }
}
