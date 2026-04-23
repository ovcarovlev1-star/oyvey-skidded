package me.alpha432.oyvey.mixin;

import me.alpha432.oyvey.features.modules.player.AntiBlind;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.entity.LivingEntity.class)
public class MixinEntity {
    @Inject(method = "hasStatusEffect", at = @At("HEAD"), cancellable = true)
    private void onHasStatusEffect(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if (AntiBlind.getInstance().isEnabled()) {
            // Если игра спрашивает, есть ли у нас слепота или тьма — говорим "нет"
            if (effect.equals(StatusEffects.BLINDNESS) || effect.equals(StatusEffects.DARKNESS)) {
                cir.setReturnValue(false);
            }
        }
    }
}
