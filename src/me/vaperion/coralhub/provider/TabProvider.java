package me.vaperion.coralhub.provider;

import com.minexd.zoot.ZootAPI;
import com.minexd.zoot.chat.Chat;
import com.minexd.zoot.profile.Profile;
import com.minexd.zoot.rank.Rank;
import com.minexd.zoot.status.StatusHandler;
import com.minexd.zoot.status.ZootServer;
import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by vape on 6/1/2020 at 12:31 PM.
 */
public class TabProvider implements LayoutProvider {
    private String serverName;


    @Override
    public TabLayout provide(Player player) {
        TabLayout layout = TabLayout.create(player);
        Profile profile = Profile.getProfiles().get(player);
        Rank activeRank = profile == null ? Rank.getDefaultRank() : profile.getActiveRank();

        // Header
        layout.set(1, 0, ChatColor.AQUA.toString() + ChatColor.BOLD + "Coral Network");
        layout.set(1, 1, ChatColor.DARK_AQUA + "Online " + ChatColor.GRAY + "- " + ChatColor.AQUA + StatusHandler.getTotalPlayers());

        // Second row
        layout.set(0, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Store");
        layout.set(1,3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Your Rank");
        layout.set(2, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Teamspeak");
        layout.set(1, 4, ZootAPI.getRankOfPlayer(player).getColor() + ZootAPI.getRankOfPlayer(player).getDisplayName());
        layout.set(0, 4, ChatColor.GRAY + "coral.gg/store");
        layout.set(2, 4, ChatColor.GRAY + "ts.coral.gg");


        return layout;
    }
}