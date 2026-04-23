package me.alpha432.oyvey.features.modules.player;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

public class GhostHand extends Module {
    private final Set<BlockPos> posList = new ObjectOpenHashSet<>();

    public GhostHand() {
        super("GhostHand", "Opens containers through walls.", Category.PLAYER, true, false, false);
    }

    @SubscribeEvent
    public void onUpdate(UpdateWalkingPlayerEvent event) {
        // Проверяем, зажата ли ПКМ и не крадется ли игрок
        if (mc.world == null || mc.player == null || !mc.options.useKey.isPressed() || mc.player.isSneaking()) return;

        // Если мы уже смотрим на контейнер в открытую, ничего не делаем
        var hit = mc.player.raycast(mc.player.getBlockInteractionRange(), mc.getRenderTickCounter().getTickProgress(true), false);
        if (mc.world.getBlockState(BlockPos.ofFloored(hit.getPos())).hasBlockEntity()) return;

        // Рассчитываем направление взгляда игрока
        Vec3d direction = new Vec3d(0, 0, 0.1)
                .rotateX(-(float) Math.toRadians(mc.player.getPitch()))
                .rotateY(-(float) Math.toRadians(mc.player.getYaw()));

        posList.clear();

        // Ищем контейнеры по линии взгляда (проходим сквозь стены)
        for (int i = 1; i < mc.player.getBlockInteractionRange() * 10; i++) {
            BlockPos pos = BlockPos.ofFloored(mc.player.getCameraPosVec(mc.getRenderTickCounter().getTickProgress(true)).add(direction.multiply(i)));

            if (posList.contains(pos)) continue;
            posList.add(pos);

            // Если нашли блок с BlockEntity (сундук, шалкер и т.д.)
            if (mc.world.getBlockState(pos).hasBlockEntity()) {
                for (Hand hand : Hand.values()) {
                    // Пытаемся взаимодействовать напрямую
                    ActionResult result = mc.interactionManager.interactBlock(
                        mc.player, 
                        hand, 
                        new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.UP, pos, true)
                    );

                    if (result.isAccepted()) {
                        mc.player.swingHand(hand);
                        // В OyVey обычно нет DoItemUseEvent, поэтому просто выходим после успешного взаимодействия
                        return;
                    }
                }
            }
        }
    }
}
