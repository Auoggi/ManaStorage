package me.auoggi.manastorage.item;

import me.auoggi.manastorage.base.BaseBoundItem;
import me.auoggi.manastorage.util.ModCapability;
import me.auoggi.manastorage.util.ModCapabilityProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaForgeCapabilities;

import java.util.Collections;

public class Linker extends BaseBoundItem {
    public Linker(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ModCapabilityProvider(super.initCapabilities(stack, nbt), Collections.singletonList(
                new ModCapability(BotaniaForgeCapabilities.COORD_BOUND_ITEM, () -> new BoundItem(stack))
        ));
    }
}
