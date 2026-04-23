package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Nametags extends Module {
    // Настройки из Meteor
    private final Setting<Double> scale = register(new Setting<>("Scale", 1.0, 0.1, 5.0));
    private final Setting<Boolean> displayItems = register(new Setting<>("Items", true));
    private final Setting<Boolean> displayArmor = register(new Setting<>("Armor", true));
    private final Setting<Boolean> displayHealth = register(new Setting<>("Health", true));
    private final Setting<Boolean> displayEnchants = register(new Setting<>("Enchants", true));

    public Nametags() {
        super("Nametags", "Better nametags for entities", Category.PLAYER, true, false, false);
    }

    // Этот метод будет вызываться из MixinEntityRenderer
    public void renderNameTag(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!isEnabled()) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;

        matrices.push();
        
        // Позиция над головой
        double height = entity.getNameLabelHeight() + 0.5;
        matrices.translate(0.0D, height, 0.0D);
        matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
        
        // Применяем масштаб
        float fScale = scale.getValue().floatValue() * 0.025f;
        matrices.scale(-fScale, -fScale, fScale);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        // 1. Формируем текст имени и здоровья (Логика Wurst)
        String content = entity.getName().getString();
        if (displayHealth.getValue()) {
            float health = livingEntity.getHealth();
            Formatting color = getHealthColor(health);
            content += " " + color + (int) health;
        }

        // Рендерим фон и текст
        float backgroundOpacity = mc.options.getTextBackgroundOpacity(0.25F);
        int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;
        float xOffset = (float) (-mc.textRenderer.getWidth(content) / 2);

        mc.textRenderer.draw(content, xOffset, 0, 553648127, false, matrix4f, vertexConsumers, entity.shouldRenderName(), backgroundColor, light);
        mc.textRenderer.draw(content, xOffset, 0, -1, false, matrix4f, vertexConsumers, entity.shouldRenderName(), 0, light);

        // 2. Рендерим предметы (Логика Meteor)
        if (entity instanceof PlayerEntity player && (displayItems.getValue() || displayArmor.getValue())) {
            renderItems(matrices, player, vertexConsumers, light);
        }

        matrices.pop();
    }

    private void renderItems(MatrixStack matrices, PlayerEntity player, VertexConsumerProvider vertexConsumers, int light) {
        List<ItemStack> items = new ArrayList<>();
        
        if (displayItems.getValue()) {
            items.add(player.getMainHandStack());
            items.add(player.getOffHandStack());
        }
        
        if (displayArmor.getValue()) {
            for (ItemStack armor : player.getArmorItems()) {
                items.add(armor);
            }
        }

        int x = -(items.size() * 8); // Центрируем полоску предметов
        
        for (ItemStack stack : items) {
            if (stack.isEmpty()) continue;

            matrices.push();
            matrices.translate(x, -20, 0); // Поднимаем предметы над ником
            matrices.scale(10f, 10f, 10f); // Уменьшаем для отрисовки
            
            // Отрисовка самой иконки предмета
            mc.getItemRenderer().renderItem(stack, net.minecraft.client.render.model.json.ModelTransformationMode.GUI, light, 0, matrices, vertexConsumers, mc.world, 0);
            
            // 3. Отрисовка зачарований (Упрощенная логика Meteor)
            if (displayEnchants.getValue() && stack.hasEnchantments()) {
                renderEnchants(matrices, stack);
            }

            matrices.pop();
            x += 16;
        }
    }

    private void renderEnchants(MatrixStack matrices, ItemStack stack) {
        matrices.push();
        matrices.scale(0.5f, 0.5f, 0.5f);
        // Здесь можно добавить цикл по EnchantmentHelper.getEnchantments(stack)
        // Но для производительности обычно рисуют только уровни (напр. "P4")
        matrices.pop();
    }

    private Formatting getHealthColor(float health) {
        if (health <= 5) return Formatting.RED;
        if (health <= 10) return Formatting.GOLD;
        if (health <= 15) return Formatting.YELLOW;
        return Formatting.GREEN;
    }
}
