package me.vaperion.coralhub.menu.cosmetics;

import me.vaperion.coralhub.menu.buttons.BackgroundButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vape on 5/31/2020 at 1:55 PM.
 */
public class MainCosmeticsMenu extends Menu {
    private static final Button BACKGROUND = new BackgroundButton();

    @Override
    public String getTitle(Player player) {
        return "Cosmetics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(13, Button.placeholder(Material.REDSTONE_BLOCK, ChatColor.RED.toString() + ChatColor.BOLD + "Work In Progress"));

        return buttons;
    }
}