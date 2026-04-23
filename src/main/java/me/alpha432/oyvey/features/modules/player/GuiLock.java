package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.HudModule;
import org.lwjgl.glfw.GLFW;
import java.awt.Color;

public class GuiLock extends Module {
    private static GuiLock INSTANCE;
    public boolean guiLocked = false;
    private boolean isKeyDown = false;

    public GuiLock() {
        super("GuiLock", "Locks GUI & hides HUD", Category.PLAYER, true, false, false);
        INSTANCE = this;
    }

    public static GuiLock getInstance() {
        if (INSTANCE == null) INSTANCE = new GuiLock();
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;

        boolean isPressed = mc.keyboard.isKeyPressed(GLFW.GLFW_KEY_BACKSPACE);

        if (isPressed && !isKeyDown) {
            guiLocked = !guiLocked;
            isKeyDown = true;

            // Находим HudModule и выключаем/включаем его
            HudModule hud = OyVey.moduleManager.getModuleByClass(HudModule.class);
            if (hud != null) {
                if (guiLocked) hud.disable(); 
                else hud.enable();
            }
        } else if (!isPressed) {
            isKeyDown = false;
        }
    }

    // Тот самый микро-индикатор (крестик)
    @Override
    public void onRender2D(Render2DEvent event) {
        if (!isEnabled() || !guiLocked) return;

        // Позиция: центр экрана по X, над хотбаром по Y
        int x = mc.getWindow().getScaledWidth() / 2;
        int y = mc.getWindow().getScaledHeight() - 24; 

        // Цвет: очень тусклый серый, чтобы не спалиться (RGBA)
        int color = new Color(180, 180, 180, 120).getRGB();

        // Рисуем крошечный крестик 3x3
        event.getContext().fill(x - 1, y, x + 2, y + 1, color); // Горизонталь
        event.getContext().fill(x, y - 1, x + 1, y + 2, color); // Вертикаль
    }

    @Override
    public boolean isVisibleInArray() { return false; }
}
