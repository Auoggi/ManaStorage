package me.auoggi.manastorage.item;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.ModCapabilities;
import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.item.ICoordBoundItem;
import vazkii.botania.api.mana.ManaBarTooltip;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//TODO Fuck Minecraft.getInstance.level as it doesn't work if in another dimension than bounded GlobalPos
//TODO Replace BasicImporterBlockEntity with CoreEntity when it is added
public class ManaStorageTablet extends Item {
    public ManaStorageTablet(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();

        if(player.isShiftKeyDown() && !level.isClientSide && level.getBlockEntity(context.getClickedPos()) instanceof BasicImporterBlockEntity entity) {
            bind(player.getItemInHand(player.getUsedItemHand()), entity);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> stacks, @NotNull TooltipFlag flags) {
        if(isBound(stack)) {
            if(isBoundLoaded(stack, Minecraft.getInstance().level)) {
                if(isBoundPowered(stack, null)) {
                    stacks.add(new TranslatableComponent("hovertext.manastorage.bound").append(ToString.GlobalPos(bound(stack))).withStyle(ChatFormatting.GRAY));
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

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ModCapabilityProvider(super.initCapabilities(stack, nbt), Arrays.asList(
                new ModCapability(ModCapabilities.manaItem, () -> new ManaItem(stack)),
                new ModCapability(BotaniaForgeCapabilities.COORD_BOUND_ITEM, () -> new CoordBoundItem(stack))
        ));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if(!level.isClientSide && getBound(stack, level.getServer()) == null) stack.getOrCreateTag().remove("bound");
    }

    private void bind(ItemStack stack, BasicImporterBlockEntity entity) {
        if(entity.getLevel() != null) {
            GlobalPos position = GlobalPos.of(entity.getLevel().dimension(), entity.getBlockPos());
            Tag nbt = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, position).get().orThrow();
            stack.getOrCreateTag().put("bound", nbt);
        }
    }

    private boolean isBound(ItemStack stack) {
        return bound(stack) != null;
    }

    private GlobalPos bound(ItemStack stack) {
        if(!stack.getOrCreateTag().contains("bound")) {
            return null;
        }

        return GlobalPos.CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().get("bound")).result().filter(position -> position.pos().getY() != Integer.MIN_VALUE).orElse(null);
    }

    private BasicImporterBlockEntity getBound(ItemStack stack, MinecraftServer server) {
        if(isBound(stack)) {
            GlobalPos pos = bound(stack);
            Level level = server != null ? server.getLevel(pos.dimension()) : Minecraft.getInstance().level;
            if(level.getChunkAt(pos.pos()).getBlockEntity(pos.pos(), LevelChunk.EntityCreationType.IMMEDIATE) instanceof BasicImporterBlockEntity entity) {
                return entity;
            }
            return null;
        }
        return null;
    }

    private boolean isBoundLoaded(ItemStack stack, Level level) {
        return isBound(stack) && level.isLoaded(bound(stack).pos());
    }

    private boolean isBoundPowered(ItemStack stack, MinecraftServer server) {
        if(isBound(stack)) {
            BasicImporterBlockEntity bound = getBound(stack, server);
            return bound != null && bound.getEnergyStorage().getEnergyStored() >= ManaStorage.basicEnergyUsage;
        }
        return false;
    }

    private boolean isBoundLoadedAndPowered(ItemStack stack, MinecraftServer server) {
        return isBound(stack) && isBoundLoaded(stack, server != null ? server.getLevel(bound(stack).dimension()) : Minecraft.getInstance().level) && isBoundPowered(stack, server);
    }

    public class ManaItem implements ModManaItem {
        private final ItemStack stack;

        public ManaItem(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public boolean isEmpty(MinecraftServer server) {
            BasicImporterBlockEntity bound = getBound(stack, server);
            return !isBoundLoadedAndPowered(stack, server) || bound == null || bound.getManaStorage().isEmpty();
        }

        @Override
        public boolean isFull(MinecraftServer server) {
            BasicImporterBlockEntity bound = getBound(stack, server);
            return !isBoundLoadedAndPowered(stack, server) || bound == null || bound.getManaStorage().isFull();
        }

        @Override
        public int receiveMana(int mana, boolean simulate, MinecraftServer server) {
            if(!isBoundLoadedAndPowered(stack, server)) return 0;
            return getBound(stack, server).getManaStorage().receiveMana(mana, simulate);
        }

        @Override
        public int extractMana(int mana, boolean simulate, MinecraftServer server) {
            if(!isBoundLoadedAndPowered(stack, server)) return 0;
            return getBound(stack, server).getManaStorage().extractMana(mana, simulate);
        }

        @Override
        public boolean canReceiveManaFromPool(BlockEntity pool, MinecraftServer server) {
            return isBoundLoadedAndPowered(stack, server);
        }

        @Override
        public boolean canReceiveManaFromItem(ItemStack otherStack, MinecraftServer server) {
            return false;
        }

        @Override
        public boolean canExportManaToPool(BlockEntity pool, MinecraftServer server) {
            return isBoundLoadedAndPowered(stack, server);
        }

        @Override
        public boolean canExportManaToItem(ItemStack otherStack, MinecraftServer server) {
            return false;
        }
    }

    public class CoordBoundItem implements ICoordBoundItem {
        private final ItemStack stack;

        public CoordBoundItem(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public BlockPos getBinding(Level level) {
            GlobalPos pos = bound(stack);
            return pos.dimension() == level.dimension() ? pos.pos() : null;
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return isBoundLoadedAndPowered(stack, null) ? Optional.of(new ManaBarTooltip((float) getBound(stack, null).getManaStorage().getManaStoredFraction())) : Optional.empty();
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return isBoundLoadedAndPowered(stack, null);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(13 * (float) getBound(stack, null).getManaStorage().getManaStoredFraction());
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return Mth.hsvToRgb((float) getBound(stack, null).getManaStorage().getManaStoredFraction() / 3.0F, 1.0F, 1.0F);
    }
}
