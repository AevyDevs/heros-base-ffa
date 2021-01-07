package net.herospvp.base_ffa.events;

import net.herospvp.base_ffa.configuration.CombatTagConfiguration;
import net.herospvp.base_ffa.database.RAM;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatTagEvents implements Listener {

    @EventHandler
    public void on(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (CombatTagConfiguration.getMapOfPlayersInCombat().get(player) >= System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "Non puoi eseguire comandi in combattimento!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player damager = (Player) event.getDamager(), damaged = (Player) event.getEntity();

        long time = System.currentTimeMillis();
        CombatTagConfiguration.getMapOfPlayersInCombat().replace(damager, time);
        CombatTagConfiguration.getMapOfPlayersInCombat().replace(damaged, time);

        CombatTagConfiguration.getLastHitter().replace(damaged, damager);
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();

        RAM.addDeaths(player, 1);

        player.getWorld().strikeLightningEffect(player.getLocation());

        event.setDeathMessage(null);
        event.getDrops().clear();

        if (CombatTagConfiguration.getLastHitter().get(player) == null
                || CombatTagConfiguration.isOutOfCombat(player)) {
            return;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (RAM.wantsNoDeaths(onlinePlayer)) continue;
            onlinePlayer.sendMessage(ChatColor.GREEN + player.getName() + " e' stato ucciso da " +
                    CombatTagConfiguration.getLastHitter().get(player));
        }

        RAM.addKills(CombatTagConfiguration.getLastHitter().get(player), 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent event) {
        CombatTagConfiguration.getMapOfPlayersInCombat().put(event.getPlayer(), 0L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (CombatTagConfiguration.getMapOfPlayersInCombat().get(player) >= System.currentTimeMillis()) {
            player.setHealth(0D);
        }

        CombatTagConfiguration.getMapOfPlayersInCombat().remove(player);
    }

}
