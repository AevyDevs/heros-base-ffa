package net.herospvp.base_ffa.events;

import net.herospvp.base_ffa.Main;
import net.herospvp.base_ffa.Memory;
import net.herospvp.base_ffa.configuration.CombatTagConfiguration;
import net.herospvp.base_ffa.configuration.WorldConfiguration;
import net.herospvp.base_ffa.database.RAM;
import net.herospvp.base_ffa.utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerEvents implements Listener {

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

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        RAM.newEntry(player);
        RAM.getOnlinePlayerNames().add(player.getName());
        RAM.getLastMessageFrom().put(player, null);

        CombatTagConfiguration.getMapOfPlayersInCombat().put(player, 0L);
        CombatTagConfiguration.getLastHitter().put(player, null);

        player.teleport(WorldConfiguration.getSpawnPoint());

        Memory.getKit().setPlayerArmor(player);
        Memory.getKit().setPlayerHotBar(player);
        Memory.getKit().assignPotionEffects(player);

        event.setJoinMessage(StringFormat.translate("&a(+) &7" + player.getName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        RAM.getOnlinePlayerNames().remove(player.getName());
        RAM.getLastMessageFrom().remove(player);

        Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> {
            CombatTagConfiguration.getMapOfPlayersInCombat().remove(player);
            CombatTagConfiguration.getLastHitter().remove(player);
        }, 10L);

        event.setQuitMessage(StringFormat.translate("&c(-) &7" + player.getName()));
    }

    @EventHandler
    public void on(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        CombatTagConfiguration.getLastHitter().replace(player, null);
        CombatTagConfiguration.getMapOfPlayersInCombat().replace(player, 0L);

        Memory.getKit().setPlayerHotBar(player);
        Memory.getKit().setPlayerArmor(player);
        Memory.getKit().assignPotionEffects(player);
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        int kills = RAM.getKills(player);

        String string = StringFormat.colorChatString(player, ChatColor.GRAY);

        if (kills >= 100 && kills < 200) {
            string = StringFormat.colorChatString(player, ChatColor.DARK_GRAY);
        } else if (kills >= 200 && kills < 300) {
            string = StringFormat.colorChatString(player, ChatColor.AQUA);
        } else if (kills >= 300 && kills < 400) {
            string = StringFormat.colorChatString(player, ChatColor.BLUE);
        } else if (kills >= 400 && kills < 500) {
            string = StringFormat.colorChatString(player, ChatColor.GREEN);
        } else if (kills >= 500 && kills < 600) {
            string = StringFormat.colorChatString(player, ChatColor.DARK_GREEN);
        } else if (kills >= 600 && kills < 700) {
            string = StringFormat.colorChatString(player, ChatColor.RED);
        } else if (kills >= 700 && kills < 800) {
            string = StringFormat.colorChatString(player, ChatColor.DARK_RED);
        } else if (kills >= 800 && kills < 900) {
            string = StringFormat.colorChatString(player, ChatColor.LIGHT_PURPLE);
        } else if (kills >= 900 && kills < 1000) {
            string = StringFormat.colorChatString(player, ChatColor.DARK_PURPLE);
        } else if (kills >= 1000) {
            string = StringFormat.colorChatString(player, ChatColor.YELLOW);
        }

        event.setFormat(string);
        event.setMessage(StringFormat.translate(event.getMessage()));

        for (String s : event.getMessage().split(" ")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getName().equalsIgnoreCase(s) || RAM.wantsNoPings(p)) continue;
                p.playSound(p.getLocation(), Sound.CAT_MEOW, 1, 1);
            }
        }
    }

}
