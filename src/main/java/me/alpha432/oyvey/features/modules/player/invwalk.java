package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class InventoryWalk extends Module {
    public InventoryWalk() {
        super("InventoryWalk", "Allows you to walk while in GUIs", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        // Не даем ходить, если открыт чат (чтобы не спамить в чат при ходьбе)
        if (mc.currentScreen instanceof ChatScreen || mc.currentScreen == null) {
            return;
        }

        // Массив клавиш, которые нужно разблокировать
        KeyBinding[] keys = {
                mc.options.forwardKey,
                mc.options.backKey,
                mc.options.leftKey,
                mc.options.rightKey,
                mc.options.jumpKey,
                mc.options.sprintKey
        };

        for (KeyBinding key : keys) {
            // Проверяем, нажата ли физическая клавиша на клавиатуре
            boolean isPressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), 
                                key.getDefaultKey().getCode());
            
            // Принудительно устанавливаем состояние "нажато" для игры
            key.setPressed(isPressed);
        }
    }
}
