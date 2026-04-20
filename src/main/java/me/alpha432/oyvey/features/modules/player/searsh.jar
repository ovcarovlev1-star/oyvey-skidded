package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.client.util.math.MatrixStack;
import java.util.ArrayList;
import java.util.List;

public class Search extends Module {
    // В OyVey обычно используются FloatSetting или BooleanSetting
    private final List<BlockPos> blocks = new ArrayList<>();
    private final int range = 64; // Радиус поиска
    private Block targetBlock = Blocks.DIAMOND_ORE; // Можно вынести в настройки

    public Search() {
        super("Search", "Highlights blocks like Wurst", Category.RENDER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null || mc.player == null) return;

        // Поиск блоков — это "тяжелая" операция, делаем её раз в 20 тиков (1 сек)
        if (mc.player.age % 20 == 0) {
            searchBlocks();
        }
    }

    private void searchBlocks() {
        blocks.clear();
        BlockPos playerPos = mc.player.getBlockPos();

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() == targetBlock) {
                        blocks.add(pos);
                    }
                }
            }
        }
    }

    // Этот метод нужно вызывать из твоего RenderEvent (обычно в OyVey это onRender3D)
    public void onRender3D(MatrixStack matrixStack) {
        if (blocks.isEmpty()) return;

        for (BlockPos pos : blocks) {
            // Рисуем бокс вокруг блока
            renderBox(matrixStack, pos);
        }
    }

    private void renderBox(MatrixStack matrixStack, BlockPos pos) {
        // Здесь должен быть вызов твоего RenderUtil
        // В OyVey это обычно RenderUtil.drawBox(matrixStack, new Box(pos), color);
        
        // Пример упрощенной логики:
        Box box = new Box(pos);
        // RenderUtil.drawOutline(matrixStack, box, Color.CYAN, 2f);
    }
}
