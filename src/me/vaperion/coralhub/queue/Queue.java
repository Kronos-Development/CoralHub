package me.vaperion.coralhub.queue;

import com.google.common.io.ByteArrayDataOutput;
import com.minexd.zoot.ZootAPI;
import com.minexd.zoot.status.StatusHandler;
import com.minexd.zoot.status.ZootServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.vaperion.coralhub.Hub;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Queue {
    private final String name;
    @Setter private boolean paused;
    private List<UUID> players = new ArrayList<>();

    public void tick() {
        fixPlayers();
        if (players.isEmpty()) return;

        ZootServer server = StatusHandler.getServer(name);
        if (server == null || !server.isOnline()) return;

        Player first = Bukkit.getPlayer(players.get(0));

        if (first == null || !first.isOnline()) {
            players.remove(0);
            return;
        }

        if (server.isWhitelisted() && !first.hasPermission("zoot.staff") || paused && !first.hasPermission("zoot.admin")) return;

        new Thread(() -> {
            int tries = 0;

            try {
                while (true) {
                    if (tries >= 3) {
                        ZootServer newServer = StatusHandler.getServer(name);
                        if (newServer != null && server.isOnline()) {
                            remove(first);
                            first.sendMessage(ChatColor.RED + "Failed to connect you to " + server.getServerName() + " - you have been removed from the queue!");
                        }
                        return;
                    }

                    tries++;
                    connect(first);

                    Thread.sleep(300L);

                    if (!first.isOnline())
                        remove(first);
                }
            } catch (Exception ex) {}
        }).start();
    }

    private void connect(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(name);

        player.sendPluginMessage(Hub.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void fixPlayers() {
        players.removeIf(u -> Bukkit.getPlayer(u) == null || !Bukkit.getPlayer(u).isOnline());

        players.sort(Comparator.comparingInt(this::getStaffPriority));

        players.sort(Comparator.comparingInt(uuid -> {
            Player player = Bukkit.getPlayer((UUID) uuid);

            return player == null ? 0 : ZootAPI.getRankOfPlayer(player).getWeight();
        }).reversed());
    }

    private int getStaffPriority(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) return 0;
        return player.hasPermission("zoot.admin") ? 0 : (player.hasPermission("zoot.staff") ? 1 : Integer.MAX_VALUE);
    }

    public int getPosition(UUID uuid) {
        fixPlayers();

        if (!isMember(uuid)) return -1;
        return players.indexOf(uuid);
    }

    public int getSize() {
        fixPlayers();
        return players.size();
    }

    public boolean isMember(UUID uuid) {
        fixPlayers();
        return players.contains(uuid) || players.stream().anyMatch(u -> u.toString().equalsIgnoreCase(uuid.toString()));
    }

    public boolean add(Player player) {
        if (isMember(player.getUniqueId())) return false;

        players.add(player.getUniqueId());

        return true;
    }

    public boolean remove(Player player) {
        if (!isMember(player.getUniqueId())) return false;

        players.remove(player.getUniqueId());
        players.removeIf(u -> u.toString().equalsIgnoreCase(player.getUniqueId().toString()));

        return true;
    }

}