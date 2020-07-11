package me.vaperion.coralhub.provider;

import com.minexd.zoot.ZootAPI;
import com.minexd.zoot.rank.Rank;
import net.frozenorb.qlib.nametag.NametagInfo;
import net.frozenorb.qlib.nametag.NametagProvider;
import org.bukkit.entity.Player;

public class NameProvider extends NametagProvider {

    public NameProvider() {
        super("Hub Tag Provider", 100);
    }

    @Override
    public NametagInfo fetchNametag(Player target, Player viewer) {
        Rank rank = ZootAPI.getRankOfPlayer(target);
        String prefix = rank == null ? "§r" : rank.getColor().toString();
        return createNametag(prefix, "");
    }
}