package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.impl.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class FreeLook extends Module {
    private final Setting<Mode> mode = register(new Setting<>("Mode", Mode.Camera));
    private final Setting<Boolean> togglePerspective = register(new Setting<>("TogglePerspective", true));
    private final Setting<Double> sensitivity = register(new Setting<>("Sensitivity", 8.0, 0.0, 10.0));

    public float cameraYaw;
    public float cameraPitch;
    private Perspective prePers;

    private static FreeLook INSTANCE = new FreeLook();

    public FreeLook() {
        super("FreeLook", "Rotate camera independently", Category.PLAYER, true, false, false);
        setInstance();
    }

    public static FreeLook getInstance() {
        if (INSTANCE == null) INSTANCE = new FreeLook();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (mc.player == null) return;
        cameraYaw = mc.player.getYaw();
        cameraPitch = mc.player.getPitch();
        prePers = mc.options.getPerspective();

        if (togglePerspective.getValue()) {
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        }
    }

    @Override
    public void onDisable() {
        if (mc.options.getPerspective() != prePers && togglePerspective.getValue()) {
            mc.options.setPerspective(prePers);
        }
    }

    // Эти методы будут вызываться из миксинов Mouse и Camera
    public boolean cameraMode() {
        return isEnabled() && mode.getValue() == Mode.Camera;
    }

    public enum Mode {
        Player,
        Camera
    }

    // В 1.21 мы можем обрабатывать это здесь или через миксин Mouse
    public void handleMouseInput(double x, double y) {
        if (!cameraMode()) return;
        
        float sens = (float) (sensitivity.getValue() * 0.1f);
        cameraYaw += (float) (x * sens);
        cameraPitch += (float) (y * sens);
        cameraPitch = MathHelper.clamp(cameraPitch, -90, 90);
    }
}
