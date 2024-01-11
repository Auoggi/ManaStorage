package me.auoggi.manastorage.mixin;

import com.google.common.collect.Iterables;
import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.util.ModBoundItem;
import me.auoggi.manastorage.util.ModManaItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.ArrayList;
import java.util.List;

//Remap set to false and marking mixin as @Pseudo makes it possible to mix into classes that don't exist at runtime - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Pseudo.html
@Pseudo @Mixin(value = HUDHandler.class, remap = false)
public class HUDHandlerMixin {
    @Unique
    private static List<Integer> manastorage$getValues() {
        Player player = Minecraft.getInstance().player;
        List<ItemStack> stacks = new ArrayList<>();

        for(ItemStack stack : Iterables.concat(player.getInventory().items, player.getInventory().offhand)) {
            if(!stack.isEmpty() && ModManaItem.of(stack) != null) {
                stacks.add(stack);
            }
        }

        int totalMana = 0;
        int totalMaxMana = 0;
        for(ItemStack stack : stacks) {
            ModManaItem modManaItem = ModManaItem.of(stack);
            if(IXplatAbstractions.INSTANCE.findCoordBoundItem(stack) instanceof ModBoundItem modBoundItem) {
                if(modManaItem != null && ManaStorage.coreClientDataMap.containsKey(modBoundItem.getBinding()) && ManaStorage.coreClientDataMap.get(modBoundItem.getBinding()).powered()) {
                    totalMana += (int) ManaStorage.coreClientDataMap.get(modBoundItem.getBinding()).mana();
                    totalMaxMana += (int) ManaStorage.coreClientDataMap.get(modBoundItem.getBinding()).capacity();
                }
            }
        }
        List<Integer> returnList = new ArrayList<>();
        returnList.add(totalMana);
        returnList.add(totalMaxMana);
        return returnList;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "onDrawScreenPost", at = @At(value = "STORE", ordinal = 0), name = "totalMana")
    private static int totalMana(int value) {
        return value + manastorage$getValues().get(0);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "onDrawScreenPost", at = @At(value = "STORE", ordinal = 0), name = "totalMaxMana")
    private static int totalMaxMana(int value) {
        return value + manastorage$getValues().get(1);
    }

    /*@Inject(method = "onDrawScreenPost", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getInventory()Lnet/minecraft/world/entity/player/Inventory;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onDrawScreenPost(PoseStack ms, float partialTicks, CallbackInfo ci, Minecraft mc, ProfilerFiller profiler, ItemStack main, ItemStack offhand, HitResult pos, Player player, int totalMana, int totalMaxMana) {
        List<ItemStack> stacks = new ArrayList<>();

        for(ItemStack stack : Iterables.concat(player.getInventory().items, player.getInventory().offhand)) {
            if(!stack.isEmpty() && ModManaItem.of(stack) != null) {
                stacks.add(stack);
            }
        }

        for(ItemStack stack : stacks) {
            ModManaItem modManaItem = ModManaItem.of(stack);
            if(IXplatAbstractions.INSTANCE.findCoordBoundItem(stack) instanceof ModBoundItem modBoundItem) {
                if(modManaItem != null && stack.is(ModItems.testItem.get()) && ManaStorage.coreClientDataMap.containsKey(modBoundItem.getBinding()) && ManaStorage.coreClientDataMap.get(modBoundItem.getBinding()).powered()) {
                    totalMana += (int) ManaStorage.coreClientDataMap.get(modBoundItem.getBinding()).mana();//TODO doesn't work use modify variable
                    totalMaxMana += (int) ManaStorage.coreClientDataMap.get(modBoundItem.getBinding()).capacity();//TODO doesn't work use modify variable
                }
            }
        }
    }*/
}
