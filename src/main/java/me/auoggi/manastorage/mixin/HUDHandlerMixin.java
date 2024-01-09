package me.auoggi.manastorage.mixin;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.ModItems;
import me.auoggi.manastorage.util.ModManaItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.client.gui.HUDHandler;

import java.util.ArrayList;
import java.util.List;

//Remap set to false and marking mixin as @Pseudo makes it possible to mix into classes that don't exist at runtime - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Pseudo.html
//Priority of Integer.MAX_VALUE will ensure it is always dead last - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Mixin.html#priority--
@Pseudo @Mixin(value = HUDHandler.class, remap = false, priority = Integer.MAX_VALUE)
public class HUDHandlerMixin {
    @Inject(method = "onDrawScreenPost", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getInventory()Lnet/minecraft/world/entity/player/Inventory;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onDrawScreenPost(PoseStack ms, float partialTicks, CallbackInfo ci, Minecraft mc, ProfilerFiller profiler, ItemStack main, ItemStack offhand, HitResult pos, Player player, int totalMana, int totalMaxMana) {
        List<ItemStack> stacks = new ArrayList<>();

        for(ItemStack stack : Iterables.concat(player.getInventory().items, player.getInventory().offhand)) {
            if(!stack.isEmpty() && ModManaItem.of(stack) != null) {
                stacks.add(stack);
            }
        }

        for(ItemStack stack : stacks) {
            ModManaItem modManaItem = ModManaItem.of(stack);
            GlobalPos bound = null; //TODO get bound position of stack
            if(modManaItem != null && stack.is(ModItems.testItem.get()) && ManaStorage.coreClientDataMap.containsKey(bound) && ManaStorage.coreClientDataMap.get(bound).powered()) {
                totalMana += (int) ManaStorage.coreClientDataMap.get(bound).mana();
                totalMaxMana += (int) ManaStorage.coreClientDataMap.get(bound).capacity();
            }
        }
    }
}
