package me.auoggi.manastorage.util;

import net.minecraftforge.common.capabilities.Capability;

import java.util.function.Supplier;

public class ModCapability {
    public final Capability<?> capability;
    public final Supplier<?> value;

    public ModCapability(Capability<?> capabilities, Supplier<?> value) {
        this.capability = capabilities;
        this.value = value;
    }
}
