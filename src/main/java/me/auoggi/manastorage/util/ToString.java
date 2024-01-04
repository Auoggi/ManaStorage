package me.auoggi.manastorage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;

public class ToString {
    public static String BlockPos(GlobalPos blockPos) {
        return blockPos != null ? blockPos.pos().getX() + ", " + blockPos.pos().getY() + ", " + blockPos.pos().getZ() : "";
    }
}
