package me.auoggi.manastorage.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.screen.render.EnergyInfoArea;
import me.auoggi.manastorage.screen.render.ManaInfoArea;
import me.auoggi.manastorage.util.MouseUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class BaseContainerScreen<M extends BaseContainerMenu<?>> extends AbstractContainerScreen<M> {
    private final ResourceLocation texture;
    private EnergyInfoArea energyInfoArea;
    private ManaInfoArea manaInfoArea;

    public BaseContainerScreen(M menu, Inventory inventory, Component component, String texture) {
        super(menu, inventory, component);
        this.texture = new ResourceLocation(ManaStorage.MODID, texture);
    }

    @Override
    protected void init() {
        super.init();
        assignInfoAreas();
    }

    private void assignInfoAreas() {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(menu.getBlockEntity() instanceof HasEnergyStorage entity) energyInfoArea = new EnergyInfoArea(x + 149, y + 12, entity.getEnergyStorage());
        if(menu.getBlockEntity() instanceof HasManaStorage entity) manaInfoArea = new ManaInfoArea(x + 12, y + 12, entity.getManaStorage());
    }

    @Override
    protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(MouseUtil.isMouseAbove(mouseX, mouseY, x + 149, y + 12, 15, 64)) {
            renderTooltip(poseStack, List.of(energyInfoArea.getTooltip()), Optional.empty(), mouseX - x, mouseY - y);
        } else if(MouseUtil.isMouseAbove(mouseX, mouseY, x + 12, y + 12, 15, 64)) {
            renderTooltip(poseStack, List.of(manaInfoArea.getTooltip()), Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float v, int i, int i1) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, texture);

        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
        if(menu.getBlockEntity() instanceof HasEnergyStorage) energyInfoArea.draw(poseStack);
        if(menu.getBlockEntity() instanceof HasManaStorage) manaInfoArea.draw(poseStack);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        renderTooltip(poseStack, mouseX, mouseY);
    }
}
