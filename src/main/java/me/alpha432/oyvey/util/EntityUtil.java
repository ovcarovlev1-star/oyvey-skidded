package me.alpha432.oyvey.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EntityUtil {
    
    public static boolean isHostileMob(Entity entity) {
        return entity instanceof HostileEntity;
    }

    public static boolean isPassiveMob(Entity entity) {
        return entity instanceof PassiveEntity;
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof PlayerEntity;
    }
}
