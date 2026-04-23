package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class NoRender extends Module {
    private static NoRender INSTANCE = new NoRender();

    // Настройки оверлеев (влияют на камеру игрока)
    public final Setting<Boolean> fire = register(new Setting<>("Fire", true));
    public final Setting<Boolean> portal = register(new Setting<>("Portal", true));
    public final Setting<Boolean> pumpkin = register(new Setting<>("Pumpkin", true));
    public final Setting<Boolean> nausea = register(new Setting<>("Nausea", true));
    public final Setting<Boolean> blind = register(new Setting<>("Blindness", true));
    public final Setting<Boolean> fog = register(new Setting<>("Fog", true));
    
    // Эффекты игрока
    public final Setting<Boolean> totem = register(new Setting<>("TotemAnimation", true));
    public final Setting<Boolean> hurtCam = register(new Setting<>("NoHurtCam", true));
    public final Setting<Boolean> glint = register(new Setting<>("NoEnchantGlint", false));

    public NoRender() {
        super("NoRender", "Disables overlays and effects for the player", Category.PLAYER, true, false, false);
        setInstance();
    }

    public static NoRender getInstance() {
        if (INSTANCE == null) INSTANCE = new NoRender();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    // Вспомогательные методы для использования в Mixins
    public boolean noFire() { return isEnabled() && fire.getValue(); }
    public boolean noTotem() { return isEnabled() && totem.getValue(); }
    public boolean noGlint() { return isEnabled() && glint.getValue(); }
}
