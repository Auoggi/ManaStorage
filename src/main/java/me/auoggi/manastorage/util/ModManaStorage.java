package me.auoggi.manastorage.util;

public abstract class ModManaStorage {
    private int mana = 0;
    private final int capacity;

    public int getManaStored() {
        return mana;
    }

    public double getManaStoredFraction() {
        return (double) mana / (double) capacity;
    }

    public int getFullCapacity() {
        return capacity;
    }

    public int getRemainingCapacity() {
        return capacity - mana;
    }

    public boolean isEmpty() {
        return mana <= 0;
    }

    public boolean isFull() {
        return mana >= capacity;
    }

    public ModManaStorage(int capacity) {
        this.capacity = capacity;
    }

    public int receiveMana(int mana, boolean simulate) {
        mana = Math.abs(Math.min(getRemainingCapacity(), mana));
        if(!simulate) {
            this.mana += mana;
            if(mana != 0) onManaChanged();
        }

        return mana;
    }

    public int extractMana(int mana, boolean simulate) {
        mana = Math.abs(Math.min(this.mana, mana));
        if(!simulate) {
            this.mana -= mana;
            if(mana != 0) onManaChanged();
        }

        return mana;
    }

    /**
     * Do not use without calling onManaChanged(), except for syncing between server and client.
     */
    public void setMana(int mana) {
        this.mana = Math.max(0, mana);
    }

    public abstract void onManaChanged();
}
