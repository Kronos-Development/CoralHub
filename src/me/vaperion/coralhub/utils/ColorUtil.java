package me.vaperion.coralhub.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public class ColorUtil {

    public static Color translateChatColorToColor(ChatColor chatColor) {
        switch (chatColor) {
            case AQUA:
                return Color.AQUA;
            case BLACK:
                return Color.BLACK;
            case BLUE:
                return Color.BLUE;
            case GRAY:
                return Color.SILVER;
            case DARK_AQUA:
                return Color.TEAL;
            case DARK_BLUE:
                return Color.NAVY;
            case DARK_GRAY:
                return Color.GRAY;
            case DARK_GREEN:
                return Color.GREEN;
            case DARK_PURPLE:
                return Color.PURPLE;
            case DARK_RED:
                return Color.MAROON;
            case GOLD:
                return Color.ORANGE;
            case GREEN:
                return Color.LIME;
            case LIGHT_PURPLE:
                return Color.FUCHSIA;
            case RED:
                return Color.RED;
            case WHITE:
                return Color.WHITE;
            case YELLOW:
                return Color.YELLOW;
            default:
                break;
        }

        return null;
    }

}
