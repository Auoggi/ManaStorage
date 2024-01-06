package me.auoggi.manastorage.mixin;

import me.auoggi.manastorage.util.ModManaItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.mana.TileBellows;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.xplat.BotaniaConfig;

import java.util.List;

//Remap set to false and marking mixin as @Pseudo makes it possible to mix into classes that don't exist at runtime - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Pseudo.html
//Priority of Integer.MAX_VALUE will ensure it is always dead last - see https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/org/spongepowered/asm/mixin/Mixin.html#priority--
@Pseudo @Mixin(value = TilePool.class, remap = false, priority = Integer.MAX_VALUE)
public abstract class TilePoolMixin {
    @Shadow @Final private static int CHARGE_EFFECT_EVENT;

    @Inject(method = "serverTick", at = @At(value = "TAIL"))
    private static void serverTick(Level level, BlockPos worldPosition, BlockState state, TilePool self, CallbackInfo ci) {
        boolean didSomething = false;

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition, worldPosition.offset(1, 1, 1)));
        for (ItemEntity item : items) {
            if(!item.isAlive()) {
                continue;
            }

            ItemStack stack = item.getItem();
            ModManaItem manaItem = ModManaItem.of(stack);

            if(!stack.isEmpty() && manaItem != null) {
                if((self.isOutputtingPower() && manaItem.canReceiveManaFromPool(self, level.getServer())) || (!self.isOutputtingPower() && manaItem.canExportManaToPool(self, level.getServer()))) {
                    int bellowCount = 0;
                    if(self.isOutputtingPower()) {
                        for(Direction dir : Direction.Plane.HORIZONTAL) {
                            BlockEntity tile = level.getBlockEntity(worldPosition.relative(dir));
                            if(tile instanceof TileBellows bellows && bellows.getLinkedTile() == self) {
                                bellowCount++;
                            }
                        }
                    }
                    int transferRate = 1000 * (bellowCount + 1);

                    if(self.isOutputtingPower()) {
                        if(self.getCurrentMana() > 0 && !manaItem.isFull(level.getServer())) {
                            self.receiveMana(-manaItem.receiveMana(transferRate, false, level.getServer()));
                            didSomething = true;
                        }
                    } else {
                        if(!self.isFull() && !manaItem.isEmpty(level.getServer())) {
                            self.receiveMana(manaItem.extractMana(transferRate, false, level.getServer()));
                            didSomething = true;
                        }
                    }

                    if(didSomething) {
                        if (BotaniaConfig.common().chargingAnimationEnabled() && level.random.nextInt(20) == 0) {
                            level.blockEvent(worldPosition, state.getBlock(), CHARGE_EFFECT_EVENT, self.isOutputtingPower() ? 1 : 0);
                        }
                        EntityHelper.syncItem(item);
                    }
                }
            }
        }

        if(didSomething) VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self);
    }
}
