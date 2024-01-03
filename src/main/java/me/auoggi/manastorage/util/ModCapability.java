package me.auoggi.manastorage.util;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class ModCapability implements ICapabilityProvider {
    private final ICapabilityProvider parent;
    private final Capability<?> capability;
    private final Supplier<?> value;

    public ModCapability(ICapabilityProvider parent, Capability<?> capability, Supplier<?> value) {
        this.parent = parent;
        this.capability = capability;
        this.value = value;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if (this.capability == capability) {
            return LazyOptional.of(() -> Objects.requireNonNull(this.value.get())).cast();
        } else {
            return this.parent != null ? this.parent.getCapability(capability, direction) : LazyOptional.empty();
        }
    }
}
