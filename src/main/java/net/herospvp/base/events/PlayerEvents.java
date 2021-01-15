package net.herospvp.base.events;

import lombok.Getter;
import lombok.Setter;
import net.herospvp.base.Base;
import net.herospvp.base.storage.Bank;
import net.herospvp.base.storage.configurations.CombatConfigurations;
import net.herospvp.base.storage.configurations.WorldConfiguration;
import net.herospvp.base.utils.StringFormat;
import net.herospvp.base.utils.lambdas.SpawnLambda;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerEvents implements Listener {

    private final Base instance;
    private final StringFormat stringFormat;
    private final Bank bank;
    private final CombatConfigurations cc;
    private final WorldConfiguration wc;
    @Getter @Setter
    private SpawnLambda spawnLambda;
    private final String serverVersion;

    public PlayerEvents(Base instance, SpawnLambda spawnLambda) {
        this.instance = instance;
        this.stringFormat = instance.getStringFormat();
        this.bank = instance.getBank();
        this.cc = instance.getCombatConfigurations();
        this.wc = instance.getWorldConfiguration();
        this.spawnLambda = spawnLambda;
        this.serverVersion = instance.getServerVersion();
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void on(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent event) {
        if (!instance.isLoaded()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "Il server e' in fase di caricamento, riprova tra poco!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        bank.newEntry(player);
        bank.getOnlinePlayers().add(player.getName());
        bank.getLastMessages().put(player, null);

        cc.getCombatTime().put(player, 0L);
        cc.getLastHitters().put(player, null);

        player.teleport(wc.getSpawnPoint());

        spawnLambda.func(player);

        event.setJoinMessage(stringFormat.translate("&a(+) &7" + player.getName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        bank.getOnlinePlayers().remove(player.getName());
        bank.getLastMessages().remove(player);

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

        spawnLambda.func(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        long kills = bank.getKills(player);

        String string = stringFormat.color(player, ChatColor.GRAY);

        if (kills >= 100 && kills < 200) {
            string = stringFormat.color(player, ChatColor.DARK_GRAY);
        } else if (kills >= 200 && kills < 300) {
            string = stringFormat.color(player, ChatColor.AQUA);
        } else if (kills >= 300 && kills < 400) {
            string = stringFormat.color(player, ChatColor.BLUE);
        } else if (kills >= 400 && kills < 500) {
            string = stringFormat.color(player, ChatColor.GREEN);
        } else if (kills >= 500 && kills < 600) {
            string = stringFormat.color(player, ChatColor.DARK_GREEN);
        } else if (kills >= 600 && kills < 700) {
            string = stringFormat.color(player, ChatColor.RED);
        } else if (kills >= 700 && kills < 800) {
            string = stringFormat.color(player, ChatColor.DARK_RED);
        } else if (kills >= 800 && kills < 900) {
            string = stringFormat.color(player, ChatColor.LIGHT_PURPLE);
        } else if (kills >= 900 && kills < 1000) {
            string = stringFormat.color(player, ChatColor.DARK_PURPLE);
        } else if (kills >= 1000) {
            string = stringFormat.color(player, ChatColor.YELLOW);
        }

        event.setFormat(string);
        if (player.hasPermission("base.color")) {
            event.setMessage(stringFormat.translate(event.getMessage()));
        }

        if (instance.getServerVersion() == null) {
            return;
        }

        for (String s : event.getMessage().split(" ")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getName().equalsIgnoreCase(s) || !bank.wantsPings(p)) continue;
                p.playSound(p.getLocation(), instance.getSound(), 1, 1);
                return;
            }
        }

    }

}
