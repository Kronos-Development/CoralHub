package me.vaperion.coralhub.menu.selector.buttons;

import com.minexd.zoot.status.StatusHandler;
import com.minexd.zoot.status.ZootServer;
import lombok.AllArgsConstructor;
import me.vaperion.coralhub.Hub;
import me.vaperion.coralhub.command.QueueCommands;
import me.vaperion.coralhub.queue.Queue;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ServerButton extends Button {

    private String serverName;
    private List<String> serverLore;
    private Material itemIcon;
    private byte itemIconDurability;

    private boolean isOnline() {
        ZootServer server = StatusHandler.getServer(serverName);
        return server != null && server.isOnline() && (System.currentTimeMillis() - server.getLastHeartbeat()) <= 5000L;
    }

    private boolean isWhitelisted() {
        ZootServer server = StatusHandler.getServer(serverName);
        return server != null && server.isWhitelisted() && (StatusHandler.getServer(serverName).isWhitelisted());
    }

    @Override
    public String getName(Player player) {
        return (isOnline() ? ChatColor.AQUA.toString() + ChatColor.BOLD.toString() : ChatColor.AQUA.toString()) + ChatColor.BOLD + serverName;
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();

        ZootServer server = StatusHandler.getServer(serverName);
        boolean online = isOnline();
        boolean whitelisted = isWhitelisted();

        int onlinePlayers = online ? server.getPlayers() : 0, maxPlayers = online ? server.getMaxPlayers() : 0;

        if (whitelisted)
            if(player.hasPermission("zoot.staff")) {
                lore.add(ChatColor.GREEN + "You have permission to access this server");
                lore.add("");
            }
            else
                lore.add(ChatColor.RED + "You don't have permissions to access this server");


        if (online || (whitelisted && player.hasPermission("zoot.staff"))) {
            lore.add(ChatColor.AQUA + "Players: " + ChatColor.RESET + onlinePlayers + "/" + maxPlayers);
        }

        lore.add("");

        lore.addAll(serverLore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));

        if (!serverLore.isEmpty())
            lore.add("");

        if (whitelisted && online) {
            lore.add(ChatColor.GRAY + "» " + ChatColor.RED + "Currently Whitelisted. " + ChatColor.GRAY + "«");
        }
        else if (online)
            lore.add(ChatColor.GRAY + "» " + ChatColor.AQUA + "Click to queue! " + ChatColor.GRAY + "«");
        else
            lore.add(ChatColor.RED + "This server is currently offline.");

        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        if (isWhitelisted() && isOnline())
            return Material.CHEST;
        else if (isOnline())
            return itemIcon;
        else
            return itemIcon;
    }

    @Override
    public int getAmount(Player player) {
        return 1;
    }

    @Override
    public byte getDamageValue(Player player) {
        return isOnline() ? itemIconDurability : 0;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (clickType != ClickType.LEFT || !isOnline()) return;
        QueueCommands.joinQueue(player, serverName);
    }
}