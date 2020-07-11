package me.vaperion.coralhub.menu.selector;

import lombok.AllArgsConstructor;
import me.vaperion.coralhub.menu.buttons.BackgroundButton;
import me.vaperion.coralhub.menu.buttons.CyanButton;
import me.vaperion.coralhub.menu.buttons.LightBlueButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class SelectorMenu extends Menu {
    private Map<Integer, Button> serverButtons;

    private static final Button BACKGROUND = new BackgroundButton();
    private static final Button LIGHT_BLUE = new LightBlueButton();
    private static final Button CYAN = new CyanButton();

    @Override
    public String getTitle(Player player) {
        return "Select Server";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            buttonMap.put(i, BACKGROUND);
            if(i == 1 | i == 3 | i == 5 | i == 7 | i == 9 | i == 19 | i == 27 | i == 37 | i == 39 | i == 41 | i == 43 | i == 44){
                buttonMap.put(1, LIGHT_BLUE);
                buttonMap.put(3, LIGHT_BLUE);
                buttonMap.put(5, LIGHT_BLUE);
                buttonMap.put(7, LIGHT_BLUE);
                buttonMap.put(9, LIGHT_BLUE);
                buttonMap.put(17, LIGHT_BLUE);
                buttonMap.put(27, LIGHT_BLUE);
                buttonMap.put(35, LIGHT_BLUE);
                buttonMap.put(37, LIGHT_BLUE);
                buttonMap.put(39, LIGHT_BLUE);
                buttonMap.put(41, LIGHT_BLUE);
                buttonMap.put(43, LIGHT_BLUE);

                buttonMap.put(0, CYAN);
                buttonMap.put(2, CYAN);
                buttonMap.put(4, CYAN);
                buttonMap.put(6, CYAN);
                buttonMap.put(8, CYAN);
                buttonMap.put(18, CYAN);
                buttonMap.put(26, CYAN);
                buttonMap.put(36, CYAN);
                buttonMap.put(38, CYAN);
                buttonMap.put(40, CYAN);
                buttonMap.put(42, CYAN);
                buttonMap.put(44, CYAN);
            }
        }

        buttonMap.putAll(serverButtons);

        return buttonMap;
    }
}