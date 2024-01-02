package me.auoggi.manastorage.screen.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.util.ModEnergyStorage;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class EnergyInfoArea extends GuiComponent {
    private static final ResourceLocation texture = new ResourceLocation(ManaStorage.MODID, "textures/gui/gui_elements.png");

    protected final Rect2i rect;
    private final ModEnergyStorage energy;

    public EnergyInfoArea(int x, int y, ModEnergyStorage energy)  {
        this(x, y, 15, 64, energy);
    }

    public EnergyInfoArea(int x, int y, int width, int height, ModEnergyStorage energy)  {
        rect = new Rect2i(x, y, width, height);
        this.energy = energy;
    }

    public Component getTooltip() {
        return new TextComponent(energy.getEnergyStored() + "/" + energy.getFullCapacity() + " FE");
    }

    public void draw(PoseStack poseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, texture);

        int scaledHeight = (int) (rect.getHeight() * energy.getEnergyStoredFraction());
        blit(poseStack, rect.getX(), rect.getY() + rect.getHeight() - scaledHeight, 0, rect.getHeight() - scaledHeight, rect.getWidth(), scaledHeight);
    }
}
