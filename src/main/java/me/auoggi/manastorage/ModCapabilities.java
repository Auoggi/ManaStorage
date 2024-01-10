package me.auoggi.manastorage;

import me.auoggi.manastorage.util.ModBoundItem;
import me.auoggi.manastorage.util.ModManaItem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {
    public static final Capability<ModManaItem> manaItem = CapabilityManager.get(new CapabilityToken<>() {});
}
