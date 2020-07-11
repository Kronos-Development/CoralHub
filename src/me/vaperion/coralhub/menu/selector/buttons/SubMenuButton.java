package me.vaperion.coralhub.menu.selector.buttons;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SubMenuButton extends Button {

    private String itemName;
    private List<String> itemLore;
    private Material itemIcon;
    private byte itemIconDurability;
    private Consumer<Player> openConsumer;

    @Override
    public String getName(Player player) {
        return ChatColor.translateAlternateColorCodes('&', itemName);
    }

    @Override
    public List<String> getDescription(Player player) {
        return itemLore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }

    @Override
    public Material getMaterial(Player player) {
        return itemIcon;
    }

    @Override
    public int getAmount(Player player) {
        return 1;
    }

    @Override
    public byte getDamageValue(Player player) {
        return itemIconDurability;
    }
    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (clickType == ClickType.LEFT)
            openConsumer.accept(player);
    }
}