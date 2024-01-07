package me.auoggi.manastorage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class LevelUtil {
    @Nullable
    public static BlockEntity getBlockEntity(Level level, BlockPos pos) {
        return level != null && pos != null ? level.getExistingBlockEntity(pos) : null;
    }
}
