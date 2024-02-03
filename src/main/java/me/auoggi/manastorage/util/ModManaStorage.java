package me.auoggi.manastorage.util;

public abstract class ModManaStorage {
    private long mana = 0;
    private final long capacity;

    public long getManaStored() {
        return mana;
    }

    public double getManaStoredFraction() {
        return (double) mana / (double) capacity;
    }

    public long getFullCapacity() {
        return capacity;
    }

    public long getRemainingCapacity() {
        return capacity - mana;
    }

    public boolean isEmpty() {
        return mana <= 0;
    }

    public boolean isFull() {
        return mana >= capacity;
    }

    public ModManaStorage() {
        this(0);
    }

    public ModManaStorage(long capacity) {
        this.capacity = capacity;
    }

    public long receiveMana(long mana, boolean simulate) {
        mana = Math.abs(Math.min(getRemainingCapacity(), mana));
        if(!simulate) {
            this.mana += mana;
            if(mana != 0) onManaChanged();
        }

        return mana;
    }

    public long extractMana(long mana, boolean simulate) {
        mana = Math.abs(Math.min(this.mana, mana));
        if(!simulate) {
            this.mana -= mana;
            if(mana != 0) onManaChanged();
        }

        return mana;
    }

    //Do not use without calling onManaChanged(), except for syncing between server and client.
    public void setMana(long mana) {
        this.mana = Math.max(0, Math.min(capacity, mana));
    }

    public abstract void onManaChanged();
}
