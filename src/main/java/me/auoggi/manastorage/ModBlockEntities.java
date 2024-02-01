package me.auoggi.manastorage;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.block.entity.ManaStorageBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> blockEntities =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ManaStorage.MODID);

    public static final RegistryObject<BlockEntityType<BasicImporterBlockEntity>> basicImporter = blockEntities.register("basic_mana_importer_block_entity",
            () -> BlockEntityType.Builder.of(BasicImporterBlockEntity::new, ModBlocks.basicImporter.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity>> manaStorageBlock1m = blockEntities.register("1m_mana_storage_block",
            () -> BlockEntityType.Builder.of((pos, state) -> new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock1m.get(), pos, state, "block.manastorage.1m_mana_storage_block", 1000000), ModBlocks.manaStorageBlock1m.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity>> manaStorageBlock4m = blockEntities.register("4m_mana_storage_block",
            () -> BlockEntityType.Builder.of((pos, state) -> new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock4m.get(), pos, state, "block.manastorage.4m_mana_storage_block", 4000000), ModBlocks.manaStorageBlock4m.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity>> manaStorageBlock16m = blockEntities.register("16m_mana_storage_block",
            () -> BlockEntityType.Builder.of((pos, state) -> new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock16m.get(), pos, state, "block.manastorage.16m_mana_storage_block", 16000000), ModBlocks.manaStorageBlock16m.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity>> manaStorageBlock64m = blockEntities.register("64m_mana_storage_block",
            () -> BlockEntityType.Builder.of((pos, state) -> new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock64m.get(), pos, state, "block.manastorage.64m_mana_storage_block", 64000000), ModBlocks.manaStorageBlock64m.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity>> manaStorageBlock256m = blockEntities.register("256m_mana_storage_block",
            () -> BlockEntityType.Builder.of((pos, state) -> new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock256m.get(), pos, state, "block.manastorage.256m_mana_storage_block", 256000000), ModBlocks.manaStorageBlock256m.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity>> manaStorageBlock1024m = blockEntities.register("1024m_mana_storage_block",
            () -> BlockEntityType.Builder.of((pos, state) -> new ManaStorageBlockEntity(ModBlockEntities.manaStorageBlock1024m.get(), pos, state, "block.manastorage.1024m_mana_storage_block", 1024000000), ModBlocks.manaStorageBlock1024m.get()).build(null));

    public static void register(IEventBus eventBus) {
        blockEntities.register(eventBus);
    }
}
