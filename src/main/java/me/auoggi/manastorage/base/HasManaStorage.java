package me.auoggi.manastorage.base;

import me.auoggi.manastorage.util.ModManaStorage;

public interface HasManaStorage {
    ModManaStorage getManaStorage();

    void setMana(int mana);
}
