package me.auoggi.manastorage.util;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nullable;

public class LevelUtil {
    @Nullable
    public static BlockEntity getBlockEntity(Level level, BlockPos pos) {
        return level != null && pos != null ? level.getExistingBlockEntity(pos) : null;
        //return level != null && pos != null ? level.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE) : null;
    }

    @Deprecated
    public static boolean isLoaded(BasicImporterBlockEntity entity) {
        return entity != null && System.currentTimeMillis() - entity.lastTickMillis <= 2500;
    }
}
