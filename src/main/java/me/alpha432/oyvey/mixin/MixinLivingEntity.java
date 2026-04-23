package me.alpha432.oyvey.mixin;

import me.alpha432.oyvey.features.modules.player.NoRender;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "hasStatusEffect", at = @At("HEAD"), cancellable = true)
    private void onHasStatusEffect(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        // Проверка для модуля NoRender
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().blind.getValue()) {
            if (effect.equals(StatusEffects.BLINDNESS) || effect.equals(StatusEffects.DARKNESS)) {
                cir.setReturnValue(false);
            }
        }
        
        // Если у тебя остался отдельный модуль AntiBlind, можно добавить и его:
        // if (AntiBlind.getInstance().isEnabled()) { ... }
    }
}
