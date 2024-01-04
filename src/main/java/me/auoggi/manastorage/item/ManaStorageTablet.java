package me.auoggi.manastorage.item;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.util.ModCapability;
import me.auoggi.manastorage.util.ModCapabilityProvider;
import me.auoggi.manastorage.util.ModManaStorage;
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
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.item.ICoordBoundItem;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.ManaBarTooltip;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//TODO doesn't unload
//TODO Replace BasicImporterBlockEntity with CoreEntity when it is added
public class ManaStorageTablet extends Item {
    public ManaStorageTablet(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();

        if(player != null && player.isShiftKeyDown() && !level.isClientSide && level.getBlockEntity(context.getClickedPos()) instanceof BasicImporterBlockEntity entity) {
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
            if(isBoundLoaded(ManaStorage.server, stack)) {
                stacks.add(new TranslatableComponent("hovertext.manastorage.bound").append(ToString.BlockPos(bound(stack).pos())).withStyle(ChatFormatting.GRAY));
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
                new ModCapability(BotaniaForgeCapabilities.MANA_ITEM, () -> new ManaItem(stack)),
                new ModCapability(BotaniaForgeCapabilities.COORD_BOUND_ITEM, () -> new CoordBoundItem(stack))
        ));
    }

    private void bind(ItemStack stack, BasicImporterBlockEntity entity) {
        if(entity.getLevel() != null) {
            GlobalPos position = GlobalPos.of(entity.getLevel().dimension(), entity.getBlockPos());
            Tag nbt = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, position).get().orThrow();
            stack.getOrCreateTag().put("bound", nbt);
        }
    }

    private GlobalPos bound(ItemStack stack) {
        if(!stack.getOrCreateTag().contains("bound")) {
            return null;
        }

        return GlobalPos.CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().get("bound")).result().filter(position -> position.pos().getY() != Integer.MIN_VALUE).orElse(null);
    }

    private BasicImporterBlockEntity getBound(MinecraftServer server, ItemStack stack) {
        if(isBound(stack)) {
            if(server == null) {
                System.out.println("server is null");
                return null; //return something
            }

            GlobalPos pos = bound(stack);
            if(pos == null) {
                System.out.println("pos is null");
                return null; //return something
            }

            Level level = server.getLevel(pos.dimension());
            if(level != null && level.getChunkAt(pos.pos()).getBlockEntity(pos.pos(), LevelChunk.EntityCreationType.IMMEDIATE) instanceof BasicImporterBlockEntity entity) {
                return entity;
            }

            //System.out.println("level.getBlockEntity(pos.pos()) = " + level.getBlockEntity(pos.pos()) + ", where level = " + level + " and pos.pos() = " + pos.pos());
            return null;
        }
        return null;
    }

    private boolean isBound(ItemStack stack) {
        return bound(stack) != null;
    }

    private boolean isBoundLoaded(MinecraftServer server, ItemStack stack) {
        if(server == null) return false;

        GlobalPos pos = bound(stack);
        if(pos == null) return false;

        Level level = server.getLevel(pos.dimension());
        if(level != null && level.isLoaded(pos.pos())) {
            if(getBound(server, stack) == null) {
                stack.getOrCreateTag().remove("bound");
            }
            return true;
        }
        return false;
    }

    public class ManaItem implements IManaItem {
        private final ItemStack stack;

        public ManaItem(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int getMana() {
            BasicImporterBlockEntity bound = getBound(ManaStorage.server, stack);
            return isBoundLoaded(ManaStorage.server, stack) && bound != null ? bound.getManaStorage().getManaStored() : 0;
        }

        @Override
        public int getMaxMana() {
            BasicImporterBlockEntity bound = getBound(ManaStorage.server, stack);
            return isBoundLoaded(ManaStorage.server, stack) && bound != null ? bound.getManaStorage().getFullCapacity() : 0;
        }

        @Override
        public void addMana(int mana) {
            ModManaStorage manaStorage = getBound(ManaStorage.server, stack).getManaStorage();
            if(isBoundLoaded(ManaStorage.server, stack) && manaStorage != null) {
                if(mana > 0) manaStorage.receiveMana(mana, false);
                else manaStorage.extractMana(mana, false);
            }
        }

        @Override
        public boolean canReceiveManaFromPool(BlockEntity pool) {
            return true;
        }

        @Override
        public boolean canReceiveManaFromItem(ItemStack otherStack) {
            return false;
        }

        @Override
        public boolean canExportManaToPool(BlockEntity pool) {
            return true;
        }

        @Override
        public boolean canExportManaToItem(ItemStack otherStack) {
            return false;
        }

        @Override
        public boolean isNoExport() {
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
            return pos != null && pos.dimension() == level.dimension() ? pos.pos() : null;
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return Optional.of(ManaBarTooltip.fromManaItem(stack));
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return isBoundLoaded(ManaStorage.server, stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(13 * ManaBarTooltip.getFractionForDisplay(Objects.requireNonNull(IXplatAbstractions.INSTANCE.findManaItem(stack))));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return Mth.hsvToRgb(ManaBarTooltip.getFractionForDisplay(Objects.requireNonNull(IXplatAbstractions.INSTANCE.findManaItem(stack))) / 3.0F, 1.0F, 1.0F);
    }
}
