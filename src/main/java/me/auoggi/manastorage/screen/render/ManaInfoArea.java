package me.auoggi.manastorage.screen.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.util.ModManaStorage;
import me.auoggi.manastorage.util.ToString;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ManaInfoArea extends GuiComponent {
    private static final ResourceLocation texture = new ResourceLocation(ManaStorage.MODID, "textures/gui/gui_elements.png");

    protected final Rect2i rect;
    private final ModManaStorage mana;

    public ManaInfoArea(int x, int y, ModManaStorage mana) {
        this(x, y, 15, 64, mana);
    }

    public ManaInfoArea(int x, int y, int width, int height, ModManaStorage mana) {
        rect = new Rect2i(x, y, width, height);
        this.mana = mana;
    }

    public Component getTooltip() {
        return new TextComponent(ToString.magnitude(mana.getManaStored()) + " / " + ToString.magnitude(mana.getFullCapacity()) + " Mana");
    }

    public void draw(PoseStack poseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, texture);

        int scaledHeight = (int) Math.ceil(rect.getHeight() * mana.getManaStoredFraction());
        blit(poseStack, rect.getX(), rect.getY() + rect.getHeight() - scaledHeight, 15, rect.getHeight() - scaledHeight, rect.getWidth(), scaledHeight);
    }
}
