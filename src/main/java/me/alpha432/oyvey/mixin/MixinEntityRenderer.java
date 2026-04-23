package me.alpha432.oyvey.mixin;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.player.Nametags;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void onRenderLabel(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // Проверяем, что это игрок (обычно Nametags нужны только для игроков)
        if (entity instanceof PlayerEntity player) {
            // Получаем экземпляр модуля из твоего менеджера модулей
            Nametags nametags = OyVey.moduleManager.getModuleByClass(Nametags.class);
            
            if (nametags != null && nametags.isEnabled()) {
                nametags.drawNametag(player, matrices, vertexConsumers, light);
                ci.cancel(); // Отменяем стандартный ник Minecraft
            }
        }
    }
}
