package me.auoggi.manastorage.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.auoggi.manastorage.ManaStorage;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ManaInfoArea extends GuiComponent {
    private static final ResourceLocation texture = new ResourceLocation(ManaStorage.MODID, "textures/gui/gui_elements.png");

    protected final Rect2i rect;
    final ModManaStorage manaStorage;
    final GlobalPos coreDataPos;

    public ManaInfoArea(int x, int y, ModManaStorage mana) {
        this(x, y, 15, 64, mana);
    }

    public ManaInfoArea(int x, int y, int width, int height, ModManaStorage mana) {
        rect = new Rect2i(x, y, width, height);
        manaStorage = mana;
        coreDataPos = null;
    }

    public ManaInfoArea(int x, int y, GlobalPos dataPos) {
        this(x, y, 15, 64, dataPos);
    }

    public ManaInfoArea(int x, int y, int width, int height, GlobalPos dataPos) {
        rect = new Rect2i(x, y, width, height);
        manaStorage = null;
        coreDataPos = dataPos;
    }

    public Component getTooltip() {
        long mana = 0;
        long capacity = 0;

        if(manaStorage != null) {
            mana = manaStorage.getManaStored();
            capacity = manaStorage.getFullCapacity();
        } else if(coreDataPos != null) {
            CoreData data = ManaStorage.clientCoreData.get(coreDataPos.dimension().toString()).get(coreDataPos.pos());
            mana = data.mana();
            capacity = data.capacity();
        }

        return new TextComponent(ToString.magnitude(mana) + " / " + ToString.magnitude(capacity) + " Mana");
    }

    public void draw(PoseStack poseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, texture);

        int scaledHeight = 0;
        if(manaStorage != null) {
            scaledHeight = (int) Math.ceil(rect.getHeight() * manaStorage.getManaStoredFraction());
        } else if(coreDataPos != null) {
            CoreData data = ManaStorage.clientCoreData.get(coreDataPos.dimension().toString()).get(coreDataPos.pos());
            scaledHeight = (int) Math.ceil(rect.getHeight() * data.manaFraction());
        }

        blit(poseStack, rect.getX(), rect.getY() + rect.getHeight() - scaledHeight, 15, rect.getHeight() - scaledHeight, rect.getWidth(), scaledHeight);
    }
}
