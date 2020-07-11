package me.vaperion.coralhub;

import cc.peko.neon.Neon;
import cc.peko.neon.cosmetics.Cosmetic;
import cc.peko.neon.cosmetics.CosmeticType;
import com.minexd.zoot.ZootAPI;
import com.minexd.zoot.rank.Rank;
import com.minexd.zoot.status.StatusHandler;
import com.minexd.zoot.status.ZootServer;
import lombok.Getter;
import me.vaperion.coralhub.listener.PlayerListener;
import me.vaperion.coralhub.provider.BoardProvider;
import me.vaperion.coralhub.provider.NameProvider;
import me.vaperion.coralhub.provider.TabProvider;
import me.vaperion.coralhub.queue.Queue;
import me.vaperion.coralhub.utils.ColorUtil;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.scoreboard.FrozenScoreboardHandler;
import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import net.frozenorb.qlib.scoreboard.TitleGetter;
import net.frozenorb.qlib.tab.FrozenTabHandler;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class Hub extends JavaPlugin {

    @Getter private static Hub instance;

    @Getter private Map<String, Queue> queues = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        HubConfig.of(getConfig());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        FrozenScoreboardHandler.setConfiguration(getBoardConfig());
        FrozenCommandHandler.registerAll(this);
        FrozenNametagHandler.registerProvider(new NameProvider());
        FrozenTabHandler.setLayoutProvider(new TabProvider());

        for (World world : Bukkit.getWorlds()) {
            world.setThundering(false);
            world.setStorm(false);
            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("mobGriefing", "false");
            world.setGameRuleValue("doMobGriefing", "false");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setviewdistance " + world.getName() + " 16");
        }

        Bukkit.getScheduler().runTaskTimer(this, this::refreshQueues, 40L, 40L);
        setupNeonItems();
    }

    private void refreshQueues() {
        for (ZootServer server : StatusHandler.getServers().values()) {
            if (server.getServerName().toLowerCase().startsWith("hub")
                    || server.getServerName().toLowerCase().startsWith("restricted")) continue;

            if (queues.get(server.getServerName()) == null) {
                queues.put(server.getServerName(), new Queue(server.getServerName()));
                String log = ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "Hub" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + "Created queue for " + server.getServerName();

                Bukkit.getConsoleSender().sendMessage(log);
                Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("zoot.admin")).forEach(p -> p.sendMessage(log));
            }
        }

        queues.values().forEach(Queue::tick);
    }

    private void setupNeonItems() {

        ArrayList<Rank> rankList = Rank.getRanks().values().stream().sorted(Comparator.comparingInt(Rank::getWeight).reversed()).collect(Collectors.toCollection(ArrayList::new));

        rankList.forEach(rank -> {
            if(rank.isDefaultRank()) return;
            Neon.getInstance().getCosmeticHandler().getCosmetics().add(new Cosmetic() {
                @Override
                public String getName() {
                    return rank.getDisplayName() + " Suit";
                }

                @Override
                public String getDisplayName() {
                    return rank.getColor() + rank.getDisplayName() + ChatColor.YELLOW + " Suit";
                }

                @Override
                public CosmeticType getCosmeticType() {
                    return CosmeticType.ARMOR;
                }

                @Override
                public boolean hasPermission(Player player) {
                    return ZootAPI.getRankOfPlayer(player).getWeight() >= rank.getWeight();
                }

                @Override
                public List<String> getDescription() {
                    return Arrays.asList(ChatColor.WHITE + "Suit yourself up in your one", ChatColor.WHITE + "of a kind " + rank.getColor() + rank.getDisplayName() + ChatColor.WHITE + " suit!");
                }

                @Override
                public ItemStack getIcon() {
                    ItemStack itemStack = ItemBuilder.of(Material.LEATHER_CHESTPLATE).name(getDisplayName()).color(ColorUtil.translateChatColorToColor(rank.getColor())).build();
                    if(rank.getWeight() >= 93) itemStack.addEnchantment(Enchantment.DURABILITY, 1);
                    return itemStack;
                }

                @Override
                public void apply(Player player) {
                    Color color = ColorUtil.translateChatColorToColor(rank.getColor());

                    ItemStack helmet = getColorArmor(Material.LEATHER_HELMET, color, rank);
                    ItemStack chestplate = getColorArmor(Material.LEATHER_CHESTPLATE, color, rank);
                    ItemStack leggings = getColorArmor(Material.LEATHER_LEGGINGS, color, rank);
                    ItemStack boots = getColorArmor(Material.LEATHER_BOOTS, color, rank);

                    player.getInventory().setHelmet(helmet);
                    player.getInventory().setChestplate(chestplate);
                    player.getInventory().setLeggings(leggings);
                    player.getInventory().setBoots(boots);
                    player.updateInventory();
                }

                @Override
                public void tick(Player player) {

                }

                @Override
                public void remove(Player player) {
                    unselectCosmetic(player);
                    player.getInventory().setArmorContents(null);
                    player.updateInventory();
                }

                @Override
                public boolean noPermissionHide() {
                    return true;
                }
            });
        });
    }

    private ScoreboardConfiguration getBoardConfig() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setScoreGetter(new BoardProvider());
        configuration.setTitleGetter(new TitleGetter(HubConfig.getBoardTitle()));

        return configuration;
    }

    public Queue getQueue(UUID player) {
        return queues.values().stream().filter(q -> q.isMember(player)).findFirst().orElse(null);
    }

    public ItemStack getColorArmor(Material m, Color c, Rank rank) {
        ItemStack i = new ItemStack(m, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta)i.getItemMeta();
        meta.setDisplayName(rank.getColor() + rank.getDisplayName() + ChatColor.WHITE + " Suit");
        if(rank.getWeight() >= 93) meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.setColor(c);
        i.setItemMeta(meta);
        return i;
    }

}