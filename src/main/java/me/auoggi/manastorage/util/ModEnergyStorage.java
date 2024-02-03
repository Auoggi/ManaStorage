package me.auoggi.manastorage.util;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class ModEnergyStorage implements IEnergyStorage {
    public LazyOptional<IEnergyStorage> lazy = LazyOptional.empty();

    private int energy = 0;
    private final int capacity;

    @Override
    public int getEnergyStored() {
        return energy;
    }

    public double getEnergyStoredFraction() {
        return (double) energy / (double) capacity;
    }

    public int getFullCapacity() {
        return capacity;
    }

    public int getRemainingCapacity() {
        return capacity - energy;
    }

    public boolean isEmpty() {
        return energy <= 0;
    }

    public boolean isFull() {
        return energy >= capacity;
    }

    public ModEnergyStorage(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public int receiveEnergy(int energy, boolean simulate) {
        energy = Math.abs(Math.min(getRemainingCapacity(), energy));
        if(!simulate) {
            this.energy += energy;
            if(energy != 0) onEnergyChanged();
        }

        return energy;
    }

    @Override
    public int extractEnergy(int energy, boolean simulate) {
        energy = Math.abs(Math.min(this.energy, energy));
        if(!simulate) {
            this.energy -= energy;
            if(energy != 0) onEnergyChanged();
        }

        return energy;
    }

    //Do not use without calling onEnergyChanged(), except for syncing between server and client.
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    public abstract void onEnergyChanged();

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public int getMaxEnergyStored() {
        return getFullCapacity();
    }
}
