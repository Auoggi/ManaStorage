package me.auoggi.manastorage.mixin;

import me.auoggi.manastorage.ManaStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Remap set to false and marking mixin as @Pseudo makes it possible to mix into classes that don't exist at runtime - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Pseudo.html
@Pseudo @Mixin(value = Level.class, remap = false)
public class LevelMixin {
    @Shadow @Final private ResourceKey<Level> dimension;

    @Shadow @Final public boolean isClientSide;

    @Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void tickBlockEntity(CallbackInfo ci, ProfilerFiller profilerfiller, Iterator<TickingBlockEntity> iterator, TickingBlockEntity tickingblockentity) {
        if(!isClientSide) {
            List<BlockPos> blockPosList = new ArrayList<>();
            if(ManaStorage.pendingLoadedBlockEntities.containsKey(dimension)) blockPosList.addAll(ManaStorage.pendingLoadedBlockEntities.get(dimension));
            blockPosList.add(tickingblockentity.getPos());

            ManaStorage.pendingLoadedBlockEntities.put(dimension, blockPosList);
        }
    }
}
