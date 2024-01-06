package me.auoggi.manastorage.mixin;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

//Remap set to false and marking mixin as @Pseudo makes it possible to mix into classes that don't exist at runtime - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Pseudo.html
//Priority of Integer.MAX_VALUE will ensure it is always dead last - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Mixin.html#priority--
@Pseudo @Mixin(value = Level.class, remap = false, priority = Integer.MAX_VALUE)
public class LevelMixin {
    /*@Inject(method = "tickBlockEntities", at = @At(value = "HEAD"))
    private void tickBlockEntities(CallbackInfo ci) {

    }*/

    @Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void tickBlockEntity(CallbackInfo ci, ProfilerFiller profilerfiller, Iterator<TickingBlockEntity> iterator, TickingBlockEntity tickingblockentity) {

    }
}
