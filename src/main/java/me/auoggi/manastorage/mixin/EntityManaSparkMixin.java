package me.auoggi.manastorage.mixin;

import me.auoggi.manastorage.util.ModManaItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.mana.spark.IManaSpark;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.SparkUpgradeType;
import vazkii.botania.common.entity.EntityManaSpark;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.*;

//Remap set to false and marking mixin as @Pseudo makes it possible to mix into classes that don't exist at runtime - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Pseudo.html
//Priority of Integer.MAX_VALUE will ensure it is always dead last - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Mixin.html#priority--
@Pseudo @Mixin(value = EntityManaSpark.class, remap = false, priority = Integer.MAX_VALUE)
public abstract class EntityManaSparkMixin {
    @Shadow @Final private static int TRANSFER_RATE;

    @Shadow protected abstract void particlesTowards(Entity e);

    @Unique
    private final List<Player> manastorage$receivingPlayers = new ArrayList<>();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 1, shift = At.Shift.BY, by = -2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void tick0(CallbackInfo ci, ISparkAttachable tile, IManaReceiver receiver, SparkUpgradeType upgrade, Collection<IManaSpark> transfers, List<Player> players, Map<Player, Map<IManaItem, Integer>> receivingPlayers, ItemStack input, Iterator iterator1, Player player, List<ItemStack> stacks) {
        List<ItemStack> manaItems = new ArrayList<>();

        for(ItemStack stack : stacks) {
            if(IXplatAbstractions.INSTANCE.findManaItem(stack) != null || ModManaItem.of(stack) != null) {
                manaItems.add(stack);
            }
        }

        if(!manaItems.isEmpty()) manastorage$receivingPlayers.add(player);

        //Make sure default Botania code doesn't happen
        stacks = Collections.singletonList(ItemStack.EMPTY);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void tick1(CallbackInfo ci, ISparkAttachable tile, IManaReceiver receiver, SparkUpgradeType upgrade, Collection<IManaSpark> transfers, List<Player> players, Map<Player, Map<IManaItem, Integer>> receivingPlayers, ItemStack input) {
        if(!manastorage$receivingPlayers.isEmpty()) {
            Player player = manastorage$receivingPlayers.get(new Random().nextInt(manastorage$receivingPlayers.size()));

            int manaSent = ManaItemHandler.instance().dispatchMana(input, player, Math.min(receiver.getCurrentMana(), TRANSFER_RATE), true);
            receiver.receiveMana(-manaSent);
            particlesTowards(player);
        }

        //Make sure default Botania code doesn't happen
        receivingPlayers.clear();
    }
}
