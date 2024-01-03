package me.auoggi.manastorage.util;

import net.minecraft.core.BlockPos;

public class ToString {
    public static String BlockPos(BlockPos blockPos) {
        return blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ();
    }
}
