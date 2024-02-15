package me.auoggi.manastorage.base;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.block.entity.CoreEntity;
import me.auoggi.manastorage.util.CoreData;
import me.auoggi.manastorage.util.LevelUtil;
import me.auoggi.manastorage.util.ToString;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class BaseBoundItem extends Item {
    public BaseBoundItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();

        if(player.isShiftKeyDown() && !level.isClientSide() && level.getBlockEntity(context.getClickedPos()) instanceof CoreEntity entity) {
            bind(player.getItemInHand(player.getUsedItemHand()), entity);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> stacks, @NotNull TooltipFlag flags) {
        if(isBound(stack)) {
            if(isBoundLoaded(stack, false)) {
                if(isBoundPowered(stack, null)) {
                    stacks.add(new TranslatableComponent("hovertext.manastorage.bound").append(ToString.globalPos(bound(stack))).withStyle(ChatFormatting.GRAY));
                } else {
                    stacks.add(new TranslatableComponent("hovertext.manastorage.bound_not_powered").withStyle(ChatFormatting.GRAY));
                }
            } else {
                stacks.add(new TranslatableComponent("hovertext.manastorage.bound_not_loaded").withStyle(ChatFormatting.GRAY));
            }
        } else {
            stacks.add(new TranslatableComponent("hovertext.manastorage.not_bound").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slot, boolean selected) {
        if(!level.isClientSide() && isBoundLoaded(stack, true) && getBound(stack, level.getServer()) == null) {
            stack.getOrCreateTag().remove("bound");
        }
    }

    private void bind(ItemStack stack, CoreEntity entity) {
        if(entity.getLevel() != null) {
            GlobalPos pos = GlobalPos.of(entity.getLevel().dimension(), entity.getBlockPos());
            Tag nbt = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).get().orThrow();
            stack.getOrCreateTag().put("bound", nbt);
        }
    }

    protected boolean isBound(ItemStack stack) {
        return bound(stack) != null;
    }

    protected GlobalPos bound(ItemStack stack) {
        if(!stack.getOrCreateTag().contains("bound")) {
            return null;
        }

        return GlobalPos.CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().get("bound")).result().filter(position -> position.pos().getY() != Integer.MIN_VALUE).orElse(null);
    }

    protected CoreEntity getBound(ItemStack stack, MinecraftServer server) {
        if(isBound(stack) && server != null) {
            GlobalPos pos = bound(stack);
            Level level = server.getLevel(pos.dimension());
            if(LevelUtil.getBlockEntity(level, pos.pos()) instanceof CoreEntity entity) {
                return entity;
            }
        }
        return null;
    }

    protected boolean isBoundLoaded(ItemStack stack, boolean server) {
        if(isBound(stack)) {
            GlobalPos pos = bound(stack);
            return server ? ManaStorage.loadedBlockEntities.containsKey(pos.dimension()) && ManaStorage.loadedBlockEntities.get(pos.dimension()).contains(pos.pos()) : ManaStorage.clientCoreData.containsKey(pos.dimension().toString()) && ManaStorage.clientCoreData.get(pos.dimension().toString()).containsKey(pos.pos());
        }
        return false;
    }

    protected boolean isBoundPowered(ItemStack stack, MinecraftServer server) {
        if(isBound(stack)) {
            if(server != null) {
                CoreEntity bound = getBound(stack, server);
                return bound != null && bound.powered();
            } else {
                GlobalPos pos = bound(stack);
                if(ManaStorage.clientCoreData.containsKey(pos.dimension().toString())) {
                    Map<BlockPos, CoreData> map = ManaStorage.clientCoreData.get(pos.dimension().toString());
                    if(map.containsKey(pos.pos())) {
                        return map.get(pos.pos()).powered();
                    }
                }
            }
        }
        return false;
    }

    protected boolean isBoundLoadedAndPowered(ItemStack stack, MinecraftServer server) {
        return isBoundLoaded(stack, server != null) && isBoundPowered(stack, server);
    }

    public class BoundItem implements ModBoundItem {
        private final ItemStack stack;

        public BoundItem(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public BlockPos getBinding(Level level) {
            GlobalPos pos = bound(stack);
            return pos != null && pos.dimension() == level.dimension() ? pos.pos() : null;
        }

        @Override
        public GlobalPos getBinding() {
            return bound(stack);
        }

        @Nullable
        public GlobalPos getBindingLoadedAndPowered(MinecraftServer server) {
            return isBoundLoadedAndPowered(stack, server) ? getBinding() : null;
        }
    }
}
