package me.auoggi.manastorage.mixin;

import com.google.common.collect.Iterables;
import me.auoggi.manastorage.util.ModManaItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.impl.mana.ManaItemHandlerImpl;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.*;

//Remap set to false and marking mixin as @Pseudo makes it possible to mix into classes that don't exist at runtime - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Pseudo.html
//Priority of 1 will ensure it is first - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Mixin.html#priority--
@Pseudo @Mixin(value = ManaItemHandlerImpl.class, remap = false, priority = 1)
public abstract class ManaItemHandlerImplMixin {
    @Unique
    private final Map<ModManaItem, Integer> manastorage$toRemove = new HashMap<>();

    @Unique
    private void manastorage$clearToRemove(MinecraftServer server) {
        for(Map.Entry<ModManaItem, Integer> entry : manastorage$toRemove.entrySet()) {
            entry.getKey().extractMana(entry.getValue(), false, server);
        }
    }

    @Unique
    private List<ItemStack> manastorage$getModManaItems(Player player) {
        if(player == null) {
            return Collections.emptyList();
        }

        List<ItemStack> toReturn = new ArrayList<>();

        for(ItemStack stack : Iterables.concat(player.getInventory().items, player.getInventory().offhand)) {
            if(!stack.isEmpty() && ModManaItem.of(stack) != null) {
                toReturn.add(stack);
            }
        }

        return toReturn;
    }

    @Inject(method = "requestMana", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void requestMana(ItemStack stack, Player player, int manaToGet, boolean remove, CallbackInfoReturnable<Integer> cir, List<ItemStack> items, List<ItemStack> acc, int manaReceived) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                if(manaItem == null || !manaItem.canExportManaToItem(stack, player.getServer()) || !IXplatAbstractions.INSTANCE.findManaItem(stack).canReceiveManaFromItem(itemStack)) {
                    continue;
                }

                int mana = Math.min(manaToGet - manaReceived, manaItem.getManaStored(server));

                if(remove) manaItem.extractMana(mana, false, server);

                manaReceived += mana;

                if(manaReceived >= manaToGet) break;
            }
            if(manaReceived >= manaToGet) cir.setReturnValue(manaReceived);
        }
    }

    @Inject(method = "requestManaExact", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void requestManaExact0(ItemStack stack, Player player, int manaToGet, boolean remove, CallbackInfoReturnable<Boolean> cir, List<ItemStack> items, List<ItemStack> acc, int manaReceived) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                if(manaItem == null || !manaItem.canExportManaToItem(stack, player.getServer()) || !IXplatAbstractions.INSTANCE.findManaItem(stack).canReceiveManaFromItem(itemStack)) {
                    continue;
                }

                int mana = Math.min(manaToGet - manaReceived, manaItem.getManaStored(server));

                if(remove) manastorage$toRemove.put(manaItem, mana);

                manaReceived += mana;

                if(manaReceived >= manaToGet) break;
            }
            if(manaReceived == manaToGet) {
                manastorage$clearToRemove(server);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "requestManaExact", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 1, shift = At.Shift.BEFORE))
    private void requestManaExact1(ItemStack stack, Player player, int manaToGet, boolean remove, CallbackInfoReturnable<Boolean> cir) {
        MinecraftServer server = player.getServer();
        if(server != null) manastorage$clearToRemove(server);
    }

    @Inject(method = "dispatchMana", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", shift = At.Shift.BEFORE), cancellable = true)
    private void dispatchMana(ItemStack stack, Player player, int manaToSend, boolean add, CallbackInfoReturnable<Integer> cir) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                if(manaItem == null || !manaItem.canReceiveManaFromItem(stack, player.getServer()) || !IXplatAbstractions.INSTANCE.findManaItem(stack).canExportManaToItem(itemStack)) {
                    continue;
                }

                int mana = Math.min(manaItem.getRemainingCapacity(server), manaToSend);

                if(add) manaItem.receiveMana(mana, false, server);

                cir.setReturnValue(mana);
                break;
            }
        }
    }

    @Inject(method = "dispatchManaExact", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", shift = At.Shift.BEFORE), cancellable = true)
    private void dispatchManaExact(ItemStack stack, Player player, int manaToSend, boolean add, CallbackInfoReturnable<Boolean> cir) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                if(manaItem == null || !manaItem.canReceiveManaFromItem(stack, player.getServer()) || !IXplatAbstractions.INSTANCE.findManaItem(stack).canExportManaToItem(itemStack)) {
                    continue;
                }

                int mana = Math.min(manaItem.getRemainingCapacity(server), manaToSend);

                if(mana == manaToSend) {
                    if(add) manaItem.receiveMana(mana, false, server);

                    cir.setReturnValue(true);
                    break;
                }
            }
        }
    }

    @Inject(method = "getInvocationCountForTool", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void getInvocationCountForTool(ItemStack stack, Player player, int manaToGet, CallbackInfoReturnable<Integer> cir, int cost, int invocations) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                if(manaItem == null || !manaItem.canExportManaToItem(stack, player.getServer()) || !IXplatAbstractions.INSTANCE.findManaItem(stack).canReceiveManaFromItem(itemStack) || cost == 0) {
                    continue;
                }

                int mana = manaItem.getManaStored(server);

                if(mana > cost) {
                    invocations += mana / cost;
                }
            }
        }
    }
}
