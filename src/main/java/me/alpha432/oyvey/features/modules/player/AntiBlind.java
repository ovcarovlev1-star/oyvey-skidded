package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;

public class AntiBlind extends Module {
    private static AntiBlind INSTANCE;

    public AntiBlind() {
        super("AntiBlind", "Removes blindness and darkness effects", Category.PLAYER, true, false, false);
        INSTANCE = this;
    }

    public static AntiBlind getInstance() {
        if (INSTANCE == null) INSTANCE = new AntiBlind();
        return INSTANCE;
    }
}
