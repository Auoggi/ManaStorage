package me.auoggi.manastorage.util;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;

public record CoreData(boolean powered, long mana, long capacity, double manaFraction) {
    public static CoreData of(BasicImporterBlockEntity entity) {
        return new CoreData(
                entity.getEnergyStorage().extractEnergy(entity.energyUsage(), true) >= entity.energyUsage(),
                entity.getManaStorage().getManaStored(),
                entity.getManaStorage().getFullCapacity(),
                entity.getManaStorage().getManaStoredFraction()
        );
    }
}