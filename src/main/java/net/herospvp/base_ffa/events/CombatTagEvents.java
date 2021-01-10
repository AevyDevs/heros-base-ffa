package net.herospvp.base_ffa.events;

import net.herospvp.base_ffa.Memory;
import net.herospvp.base_ffa.configuration.CombatTagConfiguration;
import net.herospvp.base_ffa.configuration.WorldConfiguration;
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
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatTagEvents implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (CombatTagConfiguration.isOutOfCombat(player)) {
            return;
        }

        player.sendMessage(ChatColor.RED + "Non puoi eseguire comandi in combattimento!");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player damager = (Player) event.getDamager(), damaged = (Player) event.getEntity();

        if (damager.getLocation().getY() >= WorldConfiguration.getPvpDisabledOver() ||
                damaged.getLocation().getY() >= WorldConfiguration.getPvpDisabledOver()) {
            event.setCancelled(true);
            return;
        }

        long time = System.currentTimeMillis();
        CombatTagConfiguration.getMapOfPlayersInCombat().replace(damager, time);
        CombatTagConfiguration.getMapOfPlayersInCombat().replace(damaged, time);

        CombatTagConfiguration.getLastHitter().replace(damaged, damager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();

        RAM.addDeaths(player, 1);

        player.getWorld().strikeLightningEffect(player.getLocation());

        event.setDeathMessage(null);
        event.getDrops().clear();

        if (CombatTagConfiguration.isOutOfCombat(player)) {
            return;
        }

        Player killer = CombatTagConfiguration.getLastHitter().get(player);

        if (killer == null) return;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (RAM.wantsNoDeaths(onlinePlayer) && player != onlinePlayer) continue;
            onlinePlayer.sendMessage(ChatColor.RED + player.getName() + " e' stato ucciso da " +
                    killer.getName());
        }

        Memory.getKit().assignKillRewards(killer);
        RAM.addKills(CombatTagConfiguration.getLastHitter().get(player), 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (CombatTagConfiguration.isOutOfCombat(player)) {
            return;
        }
        Player killer = CombatTagConfiguration.getLastHitter().get(player);
        RAM.addDeaths(player, 1);

        if (killer == null) return;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (RAM.wantsNoDeaths(onlinePlayer) && player != onlinePlayer) continue;
            onlinePlayer.sendMessage(ChatColor.RED + player.getName() + " e' stato ucciso da " +
                    killer.getName());
        }

        RAM.addKills(killer, 1);
    }

}
