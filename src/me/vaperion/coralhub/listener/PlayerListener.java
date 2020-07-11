package me.vaperion.coralhub.listener;

import com.minexd.zoot.ZootAPI;
import me.vaperion.coralhub.Hub;
import me.vaperion.coralhub.HubConfig;
import me.vaperion.coralhub.menu.cosmetics.MainCosmeticsMenu;
import me.vaperion.coralhub.menu.selector.MainSelectorMenu;
import me.vaperion.coralhub.queue.Queue;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {

    protected static final Map<UUID, Long> canUseButton = new ConcurrentHashMap<>();

    static class Items {
        static final ItemStack SELECTOR = ItemBuilder
                .of(Material.WATCH)
                .name(ChatColor.AQUA.toString() + ChatColor.BOLD + "Server Selector")
                .build();

        static final ItemStack ENDER_PEARL = ItemBuilder
                .of(Material.ENDER_PEARL)
                .name(ChatColor.BLUE.toString() + ChatColor.BOLD + "Ender Butt")
                .build();

        static final ItemStack COSMETICS = ItemBuilder
                .of(Material.ENDER_CHEST)
                .name(ChatColor.BLUE.toString() + ChatColor.BOLD + "Cosmetics")
                .build();
    }

    private Location getSpawn(World world) {
        return world.getSpawnLocation().add(0.5,0.5,0.5);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();

        player.teleport(getSpawn(player.getWorld()));

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        player.setWalkSpeed(0.3f);
        player.setAllowFlight(true);

        player.getInventory().setItem(0, Items.COSMETICS);
        player.getInventory().setItem(4, Items.SELECTOR);
        player.getInventory().setItem(8, Items.ENDER_PEARL);
        player.getInventory().setHeldItemSlot(4);
        player.updateInventory();

        for (String line : HubConfig.getJoinMessage()) {
            line = line.replace("{name}", player.getName());
            line = line.replace("{coloredname}", ZootAPI.getColoredName(player));

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }

        FrozenNametagHandler.reloadPlayer(player);
    }

    @EventHandler
    public void onDoubleJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.hasMetadata("build") || event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (player.isFlying()) {
                player.setFlying(false);
                player.setAllowFlight(false);

                player.setVelocity(player.getLocation().getDirection().multiply(1.9).setY(1.0));
                player.playSound(player.getLocation(), Sound.BLAZE_HIT, 5f, 1f);
            } else if (player.isOnGround() && !player.getAllowFlight())
                player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        canUseButton.remove(event.getPlayer().getUniqueId());

        Queue queue = Hub.getInstance().getQueue(player.getUniqueId());

        if (queue != null) {
            queue.remove(player);
            queue.fixPlayers();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        event.setRespawnLocation(getSpawn(player.getWorld()));
        Bukkit.getScheduler().runTaskLater(Hub.getInstance(), () -> {
            player.teleport(getSpawn(player.getWorld()));

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);

            player.getInventory().setItem(0, Items.COSMETICS);
            player.getInventory().setItem(4, Items.SELECTOR);
            player.getInventory().setItem(8, Items.ENDER_PEARL);
            player.getInventory().setHeldItemSlot(4);
            player.updateInventory();
        }, 1L);
    }

    @EventHandler
    public void onPlayerInteractItem(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) return;

        boolean permitted = canUseButton.getOrDefault(event.getPlayer().getUniqueId(), 0L) < System.currentTimeMillis();

        if (permitted) {
            Player player = event.getPlayer();

            if (Items.SELECTOR.isSimilar(event.getItem())) {
                event.setCancelled(true);
                player.updateInventory();
                new MainSelectorMenu(player);
            } else if (Items.COSMETICS.isSimilar(event.getItem())) {
                event.setCancelled(true);
                player.updateInventory();
                new MainCosmeticsMenu().openMenu(player);
            } else if (Items.ENDER_PEARL.isSimilar(event.getItem())) {
                event.setCancelled(true);
                player.updateInventory();

                Projectile pearl = player.launchProjectile(EnderPearl.class, player.getLocation().getDirection().multiply(1.6));
                pearl.setShooter(null);
                pearl.setPassenger(player);
            } else return;

            canUseButton.put(player.getUniqueId(), System.currentTimeMillis() + 500);
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_PEARL) {
            if (event.getEntity().getPassenger() != null)
                event.getEntity().getPassenger().teleport(event.getEntity().getPassenger().getLocation().add(0, 1, 0));
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getDismounted().getType() == EntityType.ENDER_PEARL)
            event.getDismounted().remove();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().hasMetadata("build"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().hasMetadata("build"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if (event.getFoodLevel() > ((Player) event.getEntity()).getFoodLevel())
            return;

        event.setFoodLevel(20);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            event.setCancelled(true);

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID)
            event.getEntity().teleport(getSpawn(event.getEntity().getWorld()));
    }

    @EventHandler
    public void onExplode(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("zoot.staff")) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(Hub.getInstance(), player::updateInventory, 1L);
            return;
        }

        event.getItemDrop().remove();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player clicked = (Player) event.getWhoClicked();

        if (clicked.getGameMode() != GameMode.CREATIVE || !clicked.hasPermission("zoot.staff")) {
            event.setCancelled(true);
            clicked.updateInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player clicked = (Player) event.getWhoClicked();

        if (clicked.getGameMode() != GameMode.CREATIVE || !clicked.hasPermission("zoot.staff")) {
            event.setCancelled(true);
            clicked.updateInventory();
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        InventoryHolder inventoryHolder = event.getSource().getHolder();
        if (!(inventoryHolder instanceof Player)) return;
        Player player = (Player) inventoryHolder;

        event.setCancelled(true);
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().hasPermission("zoot.staff")) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

}