package me.auoggi.manastorage;

import me.auoggi.manastorage.block.BasicImporterBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, ManaStorage.MODID);

    public static final RegistryObject<Block> testBlock = registerBlock("test_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f)));

    public static final RegistryObject<Block> basicImporter = registerBlock("basic_mana_importer",
            () -> new BasicImporterBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = blocks.register(name, block);
        ModItems.items.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties().tab(ModCreativeModeTab.CREATIVE_MODE_TAB)));
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        blocks.register(eventBus);
    }
}
