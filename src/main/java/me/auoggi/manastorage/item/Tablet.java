package me.auoggi.manastorage.item;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.ModCapabilities;
import me.auoggi.manastorage.base.BaseBoundItem;
import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.util.ModCapability;
import me.auoggi.manastorage.util.ModCapabilityProvider;
import me.auoggi.manastorage.util.ModManaItem;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.ManaBarTooltip;

import java.util.Arrays;
import java.util.Optional;

//TODO Replace BasicImporter with Core when added
public class Tablet extends BaseBoundItem {
    public Tablet(Properties properties) {
        super(properties);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ModCapabilityProvider(super.initCapabilities(stack, nbt), Arrays.asList(
                new ModCapability(ModCapabilities.manaItem, () -> new ManaItem(stack)),
                new ModCapability(BotaniaForgeCapabilities.COORD_BOUND_ITEM, () -> new BoundItem(stack))
        ));
    }

    public class ManaItem implements ModManaItem {
        private final ItemStack stack;

        public ManaItem(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public long getManaStored(MinecraftServer server) {
            BasicImporterBlockEntity bound = getBound(stack, server);
            return isBoundLoadedAndPowered(stack, server) && bound != null ? bound.getManaStorage().getManaStored() : 0;
        }

        @Override
        public double getManaStoredFraction(MinecraftServer server) {
            BasicImporterBlockEntity bound = getBound(stack, server);
            return isBoundLoadedAndPowered(stack, server) && bound != null ? bound.getManaStorage().getManaStoredFraction() : 0;
        }

        @Override
        public long getFullCapacity(MinecraftServer server) {
            BasicImporterBlockEntity bound = getBound(stack, server);
            return isBoundLoadedAndPowered(stack, server) && bound != null ? bound.getManaStorage().getFullCapacity() : 0;
        }

        @Override
        public long getRemainingCapacity(MinecraftServer server) {
            BasicImporterBlockEntity bound = getBound(stack, server);
            return isBoundLoadedAndPowered(stack, server) && bound != null ? bound.getManaStorage().getRemainingCapacity() : 0;
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
        public long receiveMana(long mana, boolean simulate, MinecraftServer server) {
            if(!isBoundLoadedAndPowered(stack, server)) return 0;
            return getBound(stack, server).getManaStorage().receiveMana(mana, simulate);
        }

        @Override
        public long extractMana(long mana, boolean simulate, MinecraftServer server) {
            if(!isBoundLoadedAndPowered(stack, server)) return 0;
            return getBound(stack, server).getManaStorage().extractMana(mana, simulate);
        }

        @Override
        public boolean canReceiveManaFromPool(BlockEntity pool, MinecraftServer server) {
            return isBoundLoadedAndPowered(stack, server);
        }

        @Override
        public boolean canReceiveManaFromItem(ItemStack otherStack, MinecraftServer server) {
            return isBoundLoadedAndPowered(stack, server);
        }

        @Override
        public boolean canExportManaToPool(BlockEntity pool, MinecraftServer server) {
            return isBoundLoadedAndPowered(stack, server);
        }

        @Override
        public boolean canExportManaToItem(ItemStack otherStack, MinecraftServer server) {
            return isBoundLoadedAndPowered(stack, server);
        }
    }

    private double manaFraction(ItemStack stack) {
        GlobalPos pos = bound(stack);
        if(ManaStorage.coreClientDataMap.containsKey(pos)) {
            return ManaStorage.coreClientDataMap.get(pos).powered() ? ManaStorage.coreClientDataMap.get(pos).manaFraction() : 0;
        }
        return 0;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return isBoundLoadedAndPowered(stack, null) ? Optional.of(new ManaBarTooltip((float) manaFraction(stack))) : Optional.empty();
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return isBoundLoadedAndPowered(stack, null);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return (int) Math.ceil(13 * manaFraction(stack));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return Mth.hsvToRgb((float) (manaFraction(stack) / 3.0F), 1.0F, 1.0F);
    }
}
