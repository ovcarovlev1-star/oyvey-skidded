package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerProfession;

public class AutoLibrarian extends Module {
    private VillagerEntity targetVillager;
    private BlockPos lecternPos;
    private boolean isBreaking = false;

    // Настройки
    public float range = 5.0f;
    public String wantedBook = "mending";

    public AutoLibrarian() {
        super("AutoLibrarian", "Auto resets librarian trades", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        // 1. Ищем жителя, если его нет
        if (targetVillager == null) {
            findVillager();
            return;
        }

        // 2. Ищем лекторий
        if (lecternPos == null) {
            findLectern();
            return;
        }

        // 3. Логика работы
        if (mc.currentScreen instanceof MerchantScreen) {
            checkTrades((MerchantScreen) mc.currentScreen);
        } else {
            // Если мы не в меню, и лекторий стоит — открываем меню жителя
            if (mc.world.getBlockState(lecternPos).getBlock() == Blocks.LECTERN) {
                if (!isBreaking) mc.interactionManager.interactEntity(mc.player, targetVillager, Hand.MAIN_HAND);
            } else {
                // Если лектория нет — ставим его
                placeLectern();
            }
        }

        if (isBreaking) {
            breakLectern();
        }
    }

    private void checkTrades(MerchantScreen screen) {
        // Логика проверки книг из Wurst (упрощено)
        boolean found = screen.getScreenHandler().getRecipes().stream()
            .anyMatch(recipe -> recipe.getSellItem().getItem() == Items.ENCHANTED_BOOK 
                && recipe.getSellItem().getTooltipData().toString().contains(wantedBook));

        if (found) {
            this.disable(); // Нашли нужную книгу — выключаем модуль
            mc.player.closeHandledScreen();
        } else {
            mc.player.closeHandledScreen();
            isBreaking = true; // Книга не та — ломаем
        }
    }

    private void placeLectern() {
        // Тут должна быть твоя логика установки блока (InventoryUtil для выбора лектория)
        // Пример: InventoryUtil.switchToItem(Items.LECTERN);
        // mc.interactionManager.interactBlock(...);
    }

    private void breakLectern() {
        if (lecternPos == null) return;
        mc.interactionManager.updateBlockBreakingProgress(lecternPos, net.minecraft.util.math.Direction.UP);
        mc.player.swingHand(Hand.MAIN_HAND);
        
        if (mc.world.getBlockState(lecternPos).isAir()) {
            isBreaking = false;
        }
    }

    private void findVillager() {
        for (net.minecraft.entity.Entity e : mc.world.getEntities()) {
            if (e instanceof VillagerEntity villager) {
                if (villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN) {
                    if (mc.player.distanceTo(villager) <= range) {
                        targetVillager = villager;
                        break;
                    }
                }
            }
        }
    }

    private void findLectern() {
        BlockPos pPos = mc.player.getBlockPos();
        for (BlockPos pos : BlockPos.iterate(pPos.add(-5, -2, -5), pPos.add(5, 2, 5))) {
            if (mc.world.getBlockState(pos).getBlock() == Blocks.LECTERN) {
                lecternPos = pos.toImmutable();
                break;
            }
        }
    }
}
