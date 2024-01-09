package me.auoggi.manastorage.util;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;

public record ManaStorageCoreClientData(boolean powered, long mana, long capacity, double manaFraction) {
    public static ManaStorageCoreClientData of(BasicImporterBlockEntity entity) {
        return new ManaStorageCoreClientData(
                entity.getEnergyStorage().extractEnergy(ManaStorage.basicEnergyUsage, true) >= ManaStorage.basicEnergyUsage,
                entity.getManaStorage().getManaStored(),
                entity.getManaStorage().getFullCapacity(),
                entity.getManaStorage().getManaStoredFraction()
        );
    }
}
