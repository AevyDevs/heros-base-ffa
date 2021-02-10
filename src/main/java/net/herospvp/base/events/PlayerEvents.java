package net.herospvp.base.events;

import net.herospvp.base.Base;
import net.herospvp.base.events.custom.SpawnEvent;
import net.herospvp.base.storage.BPlayer;
import net.herospvp.base.storage.PlayerBank;
import net.herospvp.base.storage.configurations.CombatConfigurations;
import net.herospvp.base.storage.configurations.WorldConfiguration;
import net.herospvp.base.utils.StringFormat;
import net.herospvp.database.Musician;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class PlayerEvents implements Listener {

    private final Base instance;
    private final StringFormat stringFormat;
    private final PlayerBank playerBank;
    private final CombatConfigurations cc;
    private final WorldConfiguration wc;
    private final Musician musician;

    public PlayerEvents(Base instance) {
        this.instance = instance;
        this.stringFormat = instance.getStringFormat();
        this.playerBank = instance.getPlayerBank();
        this.cc = instance.getCombatConfigurations();
        this.wc = instance.getWorldConfiguration();
        this.musician = instance.getMusician();
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDeathEvent event) {
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            event.getEntity().spigot().respawn();
        }, 3L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BPlayer bPlayer = playerBank.getBPlayerFrom(player);

        if (bPlayer == null) {
            musician.update(playerBank.load(player,
                    () -> playerBank.getLastMessages().put(playerBank.getBPlayerFrom(player), null))
            );
            musician.play();
        }

        cc.getCombatTime().put(player, 0L);
        cc.getLastHitters().put(player, null);

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            player.teleport(wc.getSpawnPoint());
        }, 10L);

        event.setJoinMessage(stringFormat.translate("&a(+) &7" + player.getName()));

        instance.getServer().getPluginManager().callEvent(new SpawnEvent(player));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BPlayer bPlayer = playerBank.getBPlayerFrom(player);

        if (bPlayer.isEdited()) {
            musician.update(playerBank.save(bPlayer));
            musician.play();
        }

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            cc.getCombatTime().remove(player);
            cc.getLastHitters().remove(player);
        }, 10L);

        event.setQuitMessage(stringFormat.translate("&c(-) &7" + player.getName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        cc.getLastHitters().replace(player, null);
        cc.getCombatTime().replace(player, 0L);

        instance.getServer().getPluginManager().callEvent(new SpawnEvent(player));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        BPlayer bPlayer = playerBank.getBPlayerFrom(player);

        long kills = bPlayer.getKills();

        ChatColor chatColor = ChatColor.GRAY;

        if (kills >= 100 && kills < 200) {
            chatColor = ChatColor.DARK_GRAY;
        } else if (kills >= 200 && kills < 300) {
            chatColor = ChatColor.AQUA;
        } else if (kills >= 300 && kills < 400) {
            chatColor = ChatColor.BLUE;
        } else if (kills >= 400 && kills < 500) {
            chatColor = ChatColor.GREEN;
        } else if (kills >= 500 && kills < 600) {
            chatColor = ChatColor.DARK_GREEN;
        } else if (kills >= 600 && kills < 700) {
            chatColor = ChatColor.RED;
        } else if (kills >= 700 && kills < 800) {
            chatColor = ChatColor.DARK_RED;
        } else if (kills >= 800 && kills < 900) {
            chatColor = ChatColor.LIGHT_PURPLE;
        } else if (kills >= 900 && kills < 1000) {
            chatColor = ChatColor.DARK_PURPLE;
        } else if (kills >= 1000) {
            chatColor = ChatColor.YELLOW;
        }

        event.setFormat(stringFormat.color(bPlayer, player, chatColor));
        if (player.hasPermission("base.color")) {
            event.setMessage(stringFormat.translate(event.getMessage()));
        }

        if (instance.getServerVersion() == null) {
            return;
        }

        for (String s : event.getMessage().split(" ")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getName().equalsIgnoreCase(s) || !bPlayer.isNoPings()) {
                    continue;
                }
                p.playSound(p.getLocation(), instance.getSound(), 1, 1);
                return;
            }
        }

    }

}
