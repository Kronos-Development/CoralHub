package me.vaperion.coralhub.provider;

import com.minexd.zoot.ZootAPI;
import com.minexd.zoot.profile.Profile;
import com.minexd.zoot.rank.Rank;
import com.minexd.zoot.status.StatusHandler;
import com.minexd.zoot.util.TimeUtil;
import me.vaperion.coralhub.Hub;
import me.vaperion.coralhub.HubConfig;
import me.vaperion.coralhub.queue.Queue;
import net.frozenorb.qlib.scoreboard.ScoreGetter;
import net.frozenorb.qlib.util.LinkedList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BoardProvider implements ScoreGetter {

    private int networkPlayers = 0;
    private long lastCache = 0L;

    @Override
    public void getScores(LinkedList<String> lines, Player player) {

        if (System.currentTimeMillis() - lastCache >= 1500L) {
            networkPlayers = StatusHandler.getTotalPlayers();//Stark.getInstance().core.getServers().getGlobalCount();
            lastCache = System.currentTimeMillis();
        }

        Profile profile = Profile.getProfiles().get(player.getUniqueId());
        Rank activeRank = profile == null ? Rank.getDefaultRank() : profile.getActiveRank();
        long rankExpiry = (profile == null || profile.getActiveGrant().isPermanent()) ? -1 : (profile.getActiveGrant().getAddedAt() + profile.getActiveGrant().getDuration());

        String rank = activeRank == null ? "Default" : (activeRank.getColor() + activeRank.getDisplayName());
        String rankWithExpiry = rank + (rankExpiry <= 0 ? "" : (ChatColor.GRAY + " (" + TimeUtil.millisToRoundedTime(rankExpiry - System.currentTimeMillis())) + ")");
        Queue queue = Hub.getInstance().getQueue(player.getUniqueId());

        if (queue == null) {
            for (String line : HubConfig.getBoardNormal()) {
                lines.add(line
                        .replace("%players%", String.valueOf(networkPlayers))
                        .replace("%rank%", rank)
                        .replace("%rankwithexpiry%", rankWithExpiry)
                );
            }
            return;
        }

        String name = queue.getName();
        int position = queue.getPosition(player.getUniqueId()) + 1;
        int size = queue.getSize();

        for (String line : HubConfig.getBoardQueue()) {
            lines.add(line
                    .replace("%players%", String.valueOf(networkPlayers))
                    .replace("%rank%", rank)
                    .replace("%rankwithexpiry%", rankWithExpiry)
                    .replace("%queue%", name)
                    .replace("%pos%", String.valueOf(position))
                    .replace("%total%", String.valueOf(size))
            );
        }
    }
}