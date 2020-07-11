package me.vaperion.coralhub.command;

import com.minexd.zoot.status.StatusHandler;
import com.minexd.zoot.status.ZootServer;
import me.vaperion.coralhub.Hub;
import me.vaperion.coralhub.queue.Queue;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class QueueCommands {

    @Command(names = {"pausequeue", "pq"}, permission = "op")
    public static void pauseQueue(Player player, @Param(name = "queue name") String queueName) {
        Queue queue = Hub.getInstance().getQueues().get(queueName.toLowerCase());
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Queue not found!");
            return;
        }

        queue.setPaused(!queue.isPaused());
        player.sendMessage((queue.isPaused() ? ChatColor.RED + queue.getName() + " is now paused" : ChatColor.GREEN + queue.getName() + " is no longer paused"));
    }

    @Command(names = {"joinqueue", "jq"}, permission = "")
    public static void joinQueue(Player player, @Param(name = "queue name") String queueName) {
        if (Hub.getInstance().getQueue(player.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "You are already in a queue.");
            return;
        }

        Queue queue = Hub.getInstance().getQueues().get(queueName.toLowerCase());
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Queue not found!");
            return;
        }

        ZootServer server = StatusHandler.getServer(queue.getName());
        if (server == null) {
            player.sendMessage(ChatColor.RED + "The server associated with this queue couldn't be found.");
            return;
        }

        if (!server.isOnline()) {
            player.sendMessage(ChatColor.RED + "You can't queue for this server as it is currently offline.");
            return;
        }

        queue.add(player);
        player.sendMessage(ChatColor.GREEN + "You have joined the queue for " + queue.getName());

        if (queue.isPaused())
            player.sendMessage(ChatColor.GRAY + "This queue is currently paused!");
    }

    @Command(names = {"leavequeue", "lq"}, permission = "")
    public static void leaveQueue(Player player) {
        Queue queue = Hub.getInstance().getQueue(player.getUniqueId());

        if (queue == null) {
            player.sendMessage(ChatColor.RED + "You are not in a queue.");
            return;
        }

        queue.remove(player);
        player.sendMessage(ChatColor.RED + "You have left the queue for " + queue.getName());
    }

}