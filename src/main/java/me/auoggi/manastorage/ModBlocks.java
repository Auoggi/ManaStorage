package me.auoggi.manastorage;

import me.auoggi.manastorage.block.CoreBlock;
import me.auoggi.manastorage.block.ImporterBlock;
import me.auoggi.manastorage.block.ManaStorageBlock;
import me.auoggi.manastorage.block.entity.ImporterEntity;
import me.auoggi.manastorage.block.entity.ManaStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

    public static final RegistryObject<Block> core = registerBlock("storage_core",
            () -> new CoreBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> basicImporter = registerBlock("basic_mana_importer",
            () -> new ImporterBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f).requiresCorrectToolForDrops()) {
                @Override
                public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
                    return new ImporterEntity(ModBlockEntities.basicImporter.get(), blockPos, blockState, ManaStorage.basicSpeed, ManaStorage.basicEnergyUsage, ManaStorage.basicEnergyCapacity, "block.manastorage.basic_mana_importer");
                }

                @Override
                public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
                    //noinspection DataFlowIssue
                    return createTickerHelper(blockEntityType, ModBlockEntities.basicImporter.get(), level.isClientSide() ? null : ImporterEntity::tick);
                }
            });

    public static final RegistryObject<Block> advancedImporter = registerBlock("advanced_mana_importer",
            () -> new ImporterBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f).requiresCorrectToolForDrops()) {
                @Override
                public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
                    return new ImporterEntity(ModBlockEntities.advancedImporter.get(), blockPos, blockState, -1, ManaStorage.advancedEnergyUsage, ManaStorage.advancedEnergyCapacity, "block.manastorage.advanced_mana_importer");
                }

                @Override
                public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
                    //noinspection DataFlowIssue
                    return createTickerHelper(blockEntityType, ModBlockEntities.advancedImporter.get(), level.isClientSide() ? null : ImporterEntity::tick);
                }
            });

    public static final RegistryObject<Block> manaStorageBlock1m = registerBlock("1m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f).requiresCorrectToolForDrops()) {
                @Override
                public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
                    return new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock1m.get(), blockPos, blockState, "block.manastorage.1m_mana_storage_block", 1000000);
                }
            });

    public static final RegistryObject<Block> manaStorageBlock4m = registerBlock("4m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f).requiresCorrectToolForDrops()) {
                @Override
                public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
                    return new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock4m.get(), blockPos, blockState, "block.manastorage.4m_mana_storage_block", 4000000);
                }
            });

    public static final RegistryObject<Block> manaStorageBlock16m = registerBlock("16m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f).requiresCorrectToolForDrops()) {
                @Override
                public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
                    return new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock16m.get(), blockPos, blockState, "block.manastorage.16m_mana_storage_block", 16000000);
                }
            });

    public static final RegistryObject<Block> manaStorageBlock64m = registerBlock("64m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f).requiresCorrectToolForDrops()) {
                @Override
                public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
                    return new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock64m.get(), blockPos, blockState, "block.manastorage.64m_mana_storage_block", 64000000);
                }
            });

    public static final RegistryObject<Block> manaStorageBlock256m = registerBlock("256m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f).requiresCorrectToolForDrops()) {
                @Override
                public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
                    return new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock256m.get(), blockPos, blockState, "block.manastorage.256m_mana_storage_block", 256000000);
                }
            });

    public static final RegistryObject<Block> manaStorageBlock1024m = registerBlock("1024m_mana_storage_block",
            () -> new ManaStorageBlock(BlockBehaviour.Properties.of(Material.METAL).strength(0.9f).requiresCorrectToolForDrops()) {
                @Override
                public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
                    return new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock1024m.get(), blockPos, blockState, "block.manastorage.1024m_mana_storage_block", 1024000000);
                }
            });

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = blocks.register(name, block);
        ModItems.items.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties().tab(ModCreativeModeTab.CREATIVE_MODE_TAB)));
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        blocks.register(eventBus);
    }
}
