package me.auoggi.manastorage.util;

import me.auoggi.manastorage.block.entity.CoreEntity;

public record CoreData(boolean powered, long mana, long capacity, double manaFraction) {
    public static CoreData of(CoreEntity entity) {
        return new CoreData(
                entity.powered(),
                entity.getManaStorage().getManaStored(),
                entity.getManaStorage().getFullCapacity(),
                entity.getManaStorage().getManaStoredFraction()
        );
    }
}
