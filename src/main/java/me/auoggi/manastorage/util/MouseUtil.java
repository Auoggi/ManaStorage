package me.auoggi.manastorage.util;

public class MouseUtil {
    public static boolean isMouseAbove(int mouseX, int mouseY, int x, int y, int width, int height) {
        return (mouseX >= x && mouseX <= x + width) && (mouseY >= y && mouseY <= y + height);
    }
}
