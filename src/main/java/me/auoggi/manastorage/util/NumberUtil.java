package me.auoggi.manastorage.util;

public class NumberUtil {
    public static String toMagnitude(long input) {
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

        return output;
    }
}
