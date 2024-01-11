package me.auoggi.manastorage.screen;

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

public class BasicImporterScreen extends AbstractContainerScreen<BasicImporterMenu> {
    private static final ResourceLocation texture = new ResourceLocation(ManaStorage.MODID, "textures/gui/basic_importer_gui.png");
    private EnergyInfoArea energyInfoArea;
    private ManaInfoArea manaInfoArea;

    public BasicImporterScreen(BasicImporterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        assignInfoAreas();
    }

    private void assignInfoAreas() {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        energyInfoArea = new EnergyInfoArea(x + 149, y + 12, menu.blockEntity.getEnergyStorage());
        manaInfoArea = new ManaInfoArea(x + 12, y + 12, menu.blockEntity.getManaStorage());
    }

    @Override
    protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
        renderInfoAreaTooltips(poseStack, mouseX, mouseY);
    }

    private void renderInfoAreaTooltips(PoseStack poseStack, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(MouseUtil.isMouseAbove(mouseX, mouseY, x + 149, y + 12, 15, 64)) {
            renderTooltip(poseStack, List.of(energyInfoArea.getTooltip()), Optional.empty(), mouseX - x, mouseY - y);
        } else if(MouseUtil.isMouseAbove(mouseX, mouseY, x + 12, y + 12, 15, 64)) {
            renderTooltip(poseStack, List.of(manaInfoArea.getTooltip()), Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, texture);

        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
        energyInfoArea.draw(poseStack);
        manaInfoArea.draw(poseStack);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        renderTooltip(poseStack, mouseX, mouseY);
    }
}
