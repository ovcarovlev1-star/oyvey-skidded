package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.block.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class Search extends Module {
    // Настройки
    public boolean drawTracers = true; // Настройка выключения трейсеров
    
    // Карта для хранения найденных объектов и их типов
    private final Map<BlockPos, BlockEntityType> foundBlocks = new HashMap<>();

    public enum BlockEntityType {
        CHEST(new Color(139, 69, 19), "Chest"),       // Коричневый
        SHULKER(new Color(255, 105, 180), "Shulker"), // Розовый
        HOPPER(new Color(128, 128, 128), "Hopper"),   // Серый
        OTHER(new Color(45, 45, 45), "Other");        // Темно-серый

        public final Color color;
        public final String name;
        BlockEntityType(Color color, String name) {
            this.color = color;
            this.name = name;
        }
    }

    public Search() {
        // Категория заменена на PLAYER
        super("Search", "Advanced BlockEntity ESP", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null) return;
        foundBlocks.clear();

        for (BlockEntity be : mc.world.blockEntities) {
            BlockEntityType type = BlockEntityType.OTHER;

            if (be instanceof ChestBlockEntity) type = BlockEntityType.CHEST;
            else if (be instanceof ShulkerBoxBlockEntity) type = BlockEntityType.SHULKER;
            else if (be instanceof HopperBlockEntity) type = BlockEntityType.HOPPER;
            
            foundBlocks.put(be.getPos(), type);
        }
    }

    public void onRender3D(MatrixStack matrixStack) {
        if (foundBlocks.isEmpty()) return;

        for (Map.Entry<BlockPos, BlockEntityType> entry : foundBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockEntityType type = entry.getValue();
            Color c = type.color;

            // 1. Получаем форму блока (Box)
            Box box = mc.world.getBlockState(pos).getCollisionShape(mc.world, pos).getBoundingBox();
            Box renderBox = box.offset(pos); 

            // 2. Рисуем бокс нужного цвета
            renderESP(matrixStack, renderBox, c);

            // 3. Рисуем трейсер (линию), если настройка включена
            if (drawTracers) {
                renderTracer(matrixStack, renderBox.getCenter(), c);
            }
        }
    }

    private void renderESP(MatrixStack matrixStack, Box box, Color color) {
        // Здесь используем твой RenderUtil
    }

    private void renderTracer(MatrixStack matrixStack, Vec3d target, Color color) {
        Vec3d start = new Vec3d(0, 0, 75) 
                .rotateX(-(float) Math.toRadians(mc.player.getPitch()))
                .rotateY(-(float) Math.toRadians(mc.player.getYaw()))
                .add(mc.player.getEyePos());
    }
}
