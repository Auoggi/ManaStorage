package me.auoggi.manastorage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;

public class ToString {
    public static String globalPos(GlobalPos globalPos) {
        return globalPos != null ? blockPos(globalPos.pos()) + " in " + globalPos.dimension().location() : "";
    }

    public static String blockPos(BlockPos blockPos) {
        return blockPos != null ? blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() : "";
    }

    public static String magnitude(long input) {
        int magnitude = (int) Math.floor(Math.log10(input));

        String output = "";
        if(magnitude < 3) {
            output = input + "";
        } else {
            if(magnitude < 6) {
                output = Math.round(input / 100d) / 10d + "k";
            } else if(magnitude < 9) {
                output = Math.round(input / 100000d) / 10d + "M";
            } else if(magnitude < 12) {
                output = Math.round(input / 100000000d) / 10d + "G";
            } else if(magnitude < 15) {
                output = Math.round(input / 100000000000d) / 10d + "T";
            }
        }

        return output.replace(".0", "");
    }
}
