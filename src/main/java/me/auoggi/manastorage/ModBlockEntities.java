package me.auoggi.manastorage;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.block.entity.storageBlock.*;
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

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity1m>> manaStorageBlock1m = blockEntities.register("1m_mana_storage_block",
            () -> BlockEntityType.Builder.of(ManaStorageBlockEntity1m::new, ModBlocks.manaStorageBlock1m.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity4m>> manaStorageBlock4m = blockEntities.register("4m_mana_storage_block",
            () -> BlockEntityType.Builder.of(ManaStorageBlockEntity4m::new, ModBlocks.manaStorageBlock1m.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity16m>> manaStorageBlock16m = blockEntities.register("16m_mana_storage_block",
            () -> BlockEntityType.Builder.of(ManaStorageBlockEntity16m::new, ModBlocks.manaStorageBlock1m.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity64m>> manaStorageBlock64m = blockEntities.register("64m_mana_storage_block",
            () -> BlockEntityType.Builder.of(ManaStorageBlockEntity64m::new, ModBlocks.manaStorageBlock1m.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity256m>> manaStorageBlock256m = blockEntities.register("256m_mana_storage_block",
            () -> BlockEntityType.Builder.of(ManaStorageBlockEntity256m::new, ModBlocks.manaStorageBlock1m.get()).build(null));

    public static final RegistryObject<BlockEntityType<ManaStorageBlockEntity1024m>> manaStorageBlock1024m = blockEntities.register("1024m_mana_storage_block",
            () -> BlockEntityType.Builder.of(ManaStorageBlockEntity1024m::new, ModBlocks.manaStorageBlock1m.get()).build(null));

    public static void register(IEventBus eventBus) {
        blockEntities.register(eventBus);
    }
}
