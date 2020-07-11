package me.vaperion.coralhub.menu.buttons;

import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CyanButton extends Button {
    @Override
    public String getName(Player player) {
        return ChatColor.GRAY.toString();
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return 9;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.STAINED_GLASS_PANE;
    }
}