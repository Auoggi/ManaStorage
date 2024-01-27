package me.auoggi.manastorage.base;

import me.auoggi.manastorage.util.ModEnergyStorage;

public interface HasEnergyStorage {
    ModEnergyStorage getEnergyStorage();

    int energyUsage();
}
