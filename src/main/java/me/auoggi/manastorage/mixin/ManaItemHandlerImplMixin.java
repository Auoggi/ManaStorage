package me.auoggi.manastorage.mixin;

import com.google.common.collect.Iterables;
import me.auoggi.manastorage.util.ModManaItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.common.impl.mana.ManaItemHandlerImpl;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.*;

//Remap set to false and marking mixin as @Pseudo makes it possible to mix into classes that don't exist at runtime - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Pseudo.html
@Pseudo @Mixin(value = ManaItemHandlerImpl.class, remap = false)
public abstract class ManaItemHandlerImplMixin {
    @Shadow protected abstract int discountManaForTool(ItemStack stack, Player player, int inCost);

    @Unique
    private final Map<ModManaItem, Integer> manastorage$toRemove = new HashMap<>();

    @Unique
    private void manastorage$clearToRemove(MinecraftServer server) {
        for(Map.Entry<ModManaItem, Integer> entry : manastorage$toRemove.entrySet()) {
            entry.getKey().extractMana(entry.getValue(), false, server);
        }
        manastorage$toRemove.clear();
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

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "requestMana", at = @At(value = "STORE", ordinal = 0), name = "manaReceived")
    private int requestMana(int value, ItemStack stack, Player player, int manaToGet, boolean remove) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            int manaReceived = 0;
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                IManaItem requester = IXplatAbstractions.INSTANCE.findManaItem(stack);
                if(manaItem == null || !manaItem.canExportManaToItem(stack, player.getServer()) || (requester != null && !requester.canReceiveManaFromItem(itemStack))) {
                    continue;
                }

                int mana = (int) Math.min(manaToGet - manaReceived, manaItem.getManaStored(server));

                if(remove) manaItem.extractMana(mana, false, server);

                manaReceived += mana;

                if(manaReceived >= manaToGet) break;
            }
            if(manaReceived >= manaToGet) return value + manaReceived;
        }
        return value;
    }

    /*@Inject(method = "requestMana", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", shift = At.Shift.BY, by = -2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void requestMana(ItemStack stack, Player player, int manaToGet, boolean remove, CallbackInfoReturnable<Integer> cir, List<ItemStack> items, List<ItemStack> acc, int manaReceived) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                IManaItem requester = IXplatAbstractions.INSTANCE.findManaItem(stack);
                if(manaItem == null || !manaItem.canExportManaToItem(stack, player.getServer()) || (requester != null && !requester.canReceiveManaFromItem(itemStack))) {
                    continue;
                }

                int mana = (int) Math.min(manaToGet - manaReceived, manaItem.getManaStored(server));

                if(remove) manaItem.extractMana(mana, false, server);

                manaReceived += mana;//TODO doesn't work use modify variable

                if(manaReceived >= manaToGet) break;
            }
            if(manaReceived >= manaToGet) cir.setReturnValue(manaReceived);
        }
    }*/

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "requestManaExact", at = @At(value = "STORE", ordinal = 0), name = "manaReceived")
    private int requestManaExact0(int value, ItemStack stack, Player player, int manaToGet, boolean remove) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            int manaReceived = 0;
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                IManaItem requester = IXplatAbstractions.INSTANCE.findManaItem(stack);
                if(manaItem == null || !manaItem.canExportManaToItem(stack, player.getServer()) || (requester != null && !requester.canReceiveManaFromItem(itemStack))) {
                    continue;
                }

                int mana = (int) Math.min(manaToGet - manaReceived, manaItem.getManaStored(server));

                if(remove) manastorage$toRemove.put(manaItem, mana);

                manaReceived += mana;

                if(manaReceived >= manaToGet) break;
            }
            if(manaReceived == manaToGet) {
                manastorage$clearToRemove(server);
                return value + manaReceived;
            }
        }
        return value;
    }

    /*@Inject(method = "requestManaExact", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 0, shift = At.Shift.BY, by = -2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void requestManaExact0(ItemStack stack, Player player, int manaToGet, boolean remove, CallbackInfoReturnable<Boolean> cir, List<ItemStack> items, List<ItemStack> acc, int manaReceived) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                IManaItem requester = IXplatAbstractions.INSTANCE.findManaItem(stack);
                if(manaItem == null || !manaItem.canExportManaToItem(stack, player.getServer()) || (requester != null && !requester.canReceiveManaFromItem(itemStack))) {
                    continue;
                }

                int mana = (int) Math.min(manaToGet - manaReceived, manaItem.getManaStored(server));

                if(remove) manastorage$toRemove.put(manaItem, mana);

                manaReceived += mana;//TODO doesn't work use modify variable

                if(manaReceived >= manaToGet) break;
            }
            if(manaReceived == manaToGet) {
                manastorage$clearToRemove(server);
                cir.setReturnValue(true);
            }
        }
    }*/

    @Inject(method = "requestManaExact", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 1, shift = At.Shift.BY, by = -2))
    private void requestManaExact1(ItemStack stack, Player player, int manaToGet, boolean remove, CallbackInfoReturnable<Boolean> cir) {
        MinecraftServer server = player.getServer();
        if(server != null) manastorage$clearToRemove(server);
    }

    @Inject(method = "dispatchMana", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", shift = At.Shift.BY, by = -2), cancellable = true)
    private void dispatchMana(ItemStack stack, Player player, int manaToSend, boolean add, CallbackInfoReturnable<Integer> cir) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                IManaItem sender = IXplatAbstractions.INSTANCE.findManaItem(stack);
                if(manaItem == null || !manaItem.canReceiveManaFromItem(stack, player.getServer()) || (sender != null && !sender.canExportManaToItem(itemStack))) {
                    continue;
                }

                int mana = (int) Math.min(manaItem.getRemainingCapacity(server), manaToSend);

                if(add) manaItem.receiveMana(mana, false, server);

                cir.setReturnValue(mana);
                break;
            }
        }
    }

    @Inject(method = "dispatchManaExact", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", shift = At.Shift.BY, by = -2), cancellable = true)
    private void dispatchManaExact(ItemStack stack, Player player, int manaToSend, boolean add, CallbackInfoReturnable<Boolean> cir) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                IManaItem sender = IXplatAbstractions.INSTANCE.findManaItem(stack);
                if(manaItem == null || !manaItem.canReceiveManaFromItem(stack, player.getServer()) || (sender != null && !sender.canExportManaToItem(itemStack))) {
                    continue;
                }

                int mana = (int) Math.min(manaItem.getRemainingCapacity(server), manaToSend);

                if(mana == manaToSend) {
                    if(add) manaItem.receiveMana(mana, false, server);

                    cir.setReturnValue(true);
                    break;
                }
            }
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "getInvocationCountForTool", at = @At(value = "STORE", ordinal = 0), name = "invocations")
    private int getInvocationCountForTool(int value, ItemStack stack, Player player, int manaToGet) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            int cost = discountManaForTool(stack, player, manaToGet);
            int invocations = 0;
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                IManaItem requester = IXplatAbstractions.INSTANCE.findManaItem(stack);
                if(manaItem == null || !manaItem.canExportManaToItem(stack, player.getServer()) || (requester != null && !requester.canReceiveManaFromItem(itemStack)) || cost == 0) {
                    continue;
                }

                int mana = (int) manaItem.getManaStored(server);

                if(mana > cost) {
                    invocations += mana / cost;
                }
            }
            return value + invocations;
        }
        return value;
    }

    /*@Inject(method = "getInvocationCountForTool", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", shift = At.Shift.BY, by = -2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void getInvocationCountForTool(ItemStack stack, Player player, int manaToGet, CallbackInfoReturnable<Integer> cir, int cost, int invocations) {
        MinecraftServer server = player.getServer();
        if(server != null) {
            for(ItemStack itemStack : manastorage$getModManaItems(player)) {
                if(itemStack == stack) continue;
                ModManaItem manaItem = ModManaItem.of(itemStack);
                IManaItem requester = IXplatAbstractions.INSTANCE.findManaItem(stack);
                if(manaItem == null || !manaItem.canExportManaToItem(stack, player.getServer()) || (requester != null && !requester.canReceiveManaFromItem(itemStack)) || cost == 0) {
                    continue;
                }

                int mana = (int) manaItem.getManaStored(server);

                if(mana > cost) {
                    invocations += mana / cost;//TODO doesn't work use modify variable
                }
            }
        }
    }*/
}
