package me.auoggi.manastorage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;

public class ToString {
    public static String GlobalPos(GlobalPos globalPos) {
        return globalPos != null ? BlockPos(globalPos.pos()) : "";
    }

    public static String BlockPos(BlockPos blockPos) {
        return blockPos != null ? blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() : "";
    }
}
