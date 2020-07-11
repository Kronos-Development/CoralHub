package me.vaperion.coralhub.menu.buttons;

import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TitleButton extends Button {
    @Override
    public String getName(Player player) {
        return ChatColor.AQUA.toString() + ChatColor.BOLD + "Coral Network";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NETHER_STAR;
    }
}