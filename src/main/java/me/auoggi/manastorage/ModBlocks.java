package me.auoggi.manastorage;

import me.auoggi.manastorage.block.BasicImporterBlock;
import me.auoggi.manastorage.block.entity.ManaStorageBlockEntity;
import me.auoggi.manastorage.block.ManaStorageBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, ManaStorage.MODID);

    public static final RegistryObject<Block> testBlock = registerBlock("test_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f)));

    public static final RegistryObject<Block> basicImporter = registerBlock("basic_mana_importer",
            () -> new BasicImporterBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f)));

    public static final RegistryObject<Block> manaStorageBlock1m = registerBlock("1m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f), ModBlockEntities.manaStorageBlock1m.get(), "block.manastorage.1m_mana_storage_block", 1000000));

    public static final RegistryObject<Block> manaStorageBlock4m = registerBlock("4m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f), ModBlockEntities.manaStorageBlock4m.get(), "block.manastorage.4m_mana_storage_block", 4000000));

    public static final RegistryObject<Block> manaStorageBlock16m = registerBlock("16m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f), ModBlockEntities.manaStorageBlock16m.get(), "block.manastorage.16m_mana_storage_block", 16000000));

    public static final RegistryObject<Block> manaStorageBlock64m = registerBlock("64m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f), ModBlockEntities.manaStorageBlock64m.get(), "block.manastorage.64m_mana_storage_block", 64000000));

    public static final RegistryObject<Block> manaStorageBlock256m = registerBlock("256m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f), ModBlockEntities.manaStorageBlock256m.get(), "block.manastorage.256m_mana_storage_block", 256000000));

    public static final RegistryObject<Block> manaStorageBlock1024m = registerBlock("1024m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f), ModBlockEntities.manaStorageBlock1024m.get(), "block.manastorage.1024m_mana_storage_block", 1024000000));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = blocks.register(name, block);
        ModItems.items.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties().tab(ModCreativeModeTab.CREATIVE_MODE_TAB)));
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        blocks.register(eventBus);
    }
}
