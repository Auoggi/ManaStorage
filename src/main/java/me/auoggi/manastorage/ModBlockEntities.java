package me.auoggi.manastorage;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
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

    public static void register(IEventBus eventBus) {
        blockEntities.register(eventBus);
    }
}
