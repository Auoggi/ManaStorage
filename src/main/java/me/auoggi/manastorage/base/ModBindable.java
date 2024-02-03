package me.auoggi.manastorage.base;

import me.auoggi.manastorage.util.ModEnergyStorage;
import me.auoggi.manastorage.util.ModManaStorage;
import net.minecraft.core.GlobalPos;

public interface ModBindable {
    boolean powered();
    GlobalPos position();
    ModManaStorage getManaStorage();
    ModEnergyStorage getEnergyStorage();
}
