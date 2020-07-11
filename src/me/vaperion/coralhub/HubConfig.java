package me.vaperion.coralhub;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@UtilityClass
public class HubConfig {

    @Getter private String boardTitle;
    @Getter private List<String> joinMessage;
    @Getter private List<String> boardNormal, boardQueue;

    public void of(FileConfiguration config) {
        boardTitle = config.getString("scoreboard.title");
        boardNormal = config.getStringList("scoreboard.normal");
        boardQueue = config.getStringList("scoreboard.queue");
        joinMessage = config.getStringList("joinmessage");
    }

}