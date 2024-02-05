package me.auoggi.manastorage.util;

import me.auoggi.manastorage.base.ModCapability;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ModCapabilityProvider implements ICapabilityProvider {
    private final ICapabilityProvider parent;
    private final List<ModCapability> capabilities;

    public ModCapabilityProvider(ICapabilityProvider parent, List<ModCapability> capabilities) {
        this.parent = parent;
        this.capabilities = capabilities;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        for(ModCapability cap : capabilities) {
            if(cap.capability == capability)
                return LazyOptional.of(() -> Objects.requireNonNull(cap.value.get())).cast();
        }
        return this.parent != null ? this.parent.getCapability(capability, direction) : LazyOptional.empty();
    }
}
