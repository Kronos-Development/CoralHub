package me.vaperion.coralhub.menu.selector;

import com.google.common.collect.ImmutableMap;
import me.vaperion.coralhub.menu.buttons.TitleButton;
import me.vaperion.coralhub.menu.selector.buttons.ServerButton;
import me.vaperion.coralhub.menu.selector.buttons.SubMenuButton;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainSelectorMenu {

    public MainSelectorMenu(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        buttonMap.put(20, new ServerButton(
                 "HCF",
                new ArrayList<>(),
                Material.DIAMOND_SWORD,
                (byte) 0
        ));

        buttonMap.put(21, new SubMenuButton(
                ChatColor.AQUA + ChatColor.BOLD.toString() + "Practice",
                new ArrayList<>(),
                Material.BOW,
                (byte) 0,
                (p) -> {
                    new SelectorMenu(ImmutableMap.of(
                            21, new ServerButton(
                                     "NA-Practice",
                                    new ArrayList<>(),
                                    Material.POTION,
                                    (byte) 2289
                            ),

                            22, new ServerButton(
                                     "EU-Practice",
                                    new ArrayList<>(),
                                    Material.POTION,
                                    (byte) 8194
                            ),

                            23, new ServerButton(
                                     "SA-Practice",
                                    new ArrayList<>(),
                                    Material.POTION,
                                    (byte) 8230
                            )

                    )).openMenu(player);
                }
        ));

        buttonMap.put(22, new TitleButton());

        buttonMap.put(23, new SubMenuButton(
                ChatColor.AQUA + ChatColor.BOLD.toString() + "UHC",
                new ArrayList<>(),
                Material.FISHING_ROD,
                (byte) 0,
                (p) -> {
                    new SelectorMenu(ImmutableMap.of(
                            21, new ServerButton(
                                     "UHC-1",
                                    new ArrayList<>(),
                                    Material.GOLDEN_APPLE,
                                    (byte) 0
                            ),

                            22, new ServerButton(
                                     "UHC-2",
                                    new ArrayList<>(),
                                    Material.GOLDEN_APPLE,
                                    (byte) 0
                            ),

                            23, new ServerButton(
                                     "UHC-3",
                                    new ArrayList<>(),
                                    Material.GOLDEN_APPLE,
                                    (byte) 0
                            )
                    )).openMenu(player);
                }
        ));

            buttonMap.put(24, new ServerButton(
                     "KitMap",
                    new ArrayList<>(),
                    Material.ENDER_CHEST,
                    (byte) 0
            ));

        if (player.hasPermission("zoot.builder")) {
            buttonMap.put(31, new ServerButton(
                    "Build",
                    new ArrayList<>(),
                    Material.GRASS,
                    (byte) 0
            ));
        }
        if (player.hasPermission("zoot.dev")) {
            buttonMap.put(13, new ServerButton(
                    "Dev",
                    new ArrayList<>(),
                    Material.LAVA_BUCKET,
                    (byte) 0
            ));
        }

        new SelectorMenu(buttonMap).openMenu(player);
    }
}