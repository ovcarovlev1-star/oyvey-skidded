package me.alpha432.oyvey.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.player.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    // Твой существующий метод для отрисовки HUD (ClickGUI, HUD модули)
    @Inject(method = "render", at = @At("RETURN"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (MinecraftClient.getInstance().inGameHud.getDebugHud().shouldShowDebugHud()) return;
        RenderSystem.setShaderColor(1, 1, 1, 1);

        Render2DEvent event = new Render2DEvent(context, tickCounter.getTickProgress(true));
        EVENT_BUS.post(event);
    }

    // --- СЕКЦИЯ NO RENDER ---

    // Метод для блокировки оверлеев (огонь, портал, тыква)
    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderOverlay(DrawContext context, Identifier texture, float opacity, CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled()) {
            // Проверка на огонь
            if (texture.getPath().contains("fire") && NoRender.getInstance().fire.getValue()) {
                ci.cancel();
            }
            // Проверка на портал
            if (texture.getPath().contains("portal") && NoRender.getInstance().portal.getValue()) {
                ci.cancel();
            }
            // Проверка на тыкву
            if (texture.getPath().contains("pumpkin") && NoRender.getInstance().pumpkin.getValue()) {
                ci.cancel();
            }
        }
    }

    // Метод для блокировки тошноты и специфического оверлея портала
    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderPortalOverlay(DrawContext context, float nauseaStrength, CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().portal.getValue()) {
            ci.cancel();
        }
    }
}
