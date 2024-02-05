package me.auoggi.manastorage.util;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.block.entity.CoreEntity;

public record CoreData(boolean powered, long mana, long capacity, double manaFraction) {
    //TODO Remove soon
    public static CoreData oldOf(BasicImporterBlockEntity entity) {
        return new CoreData(
                entity.getEnergyStorage().extractEnergy(entity.energyUsage(), true) >= entity.energyUsage(),
                entity.getManaStorage().getManaStored(),
                entity.getManaStorage().getFullCapacity(),
                entity.getManaStorage().getManaStoredFraction()
        );
    }

    public static CoreData of(CoreEntity entity) {
        return new CoreData(
                entity.powered(),
                entity.getManaStorage().getManaStored(),
                entity.getManaStorage().getFullCapacity(),
                entity.getManaStorage().getManaStoredFraction()
        );
    }
}
