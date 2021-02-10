package net.herospvp.base.events;

import net.herospvp.base.Base;
import net.herospvp.base.events.custom.CombatKillEvent;
import net.herospvp.base.storage.BPlayer;
import net.herospvp.base.storage.PlayerBank;
import net.herospvp.base.storage.configurations.CombatConfigurations;
import net.herospvp.base.storage.configurations.WorldConfiguration;
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

public class CombatEvents implements Listener {

    private final Base instance;
    private final PlayerBank playerBank;
    private final CombatConfigurations cc;
    private final WorldConfiguration wc;

    public CombatEvents(Base instance) {
        this.instance = instance;
        this.playerBank = instance.getPlayerBank();
        this.cc = instance.getCombatConfigurations();
        this.wc = instance.getWorldConfiguration();
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (cc.isOutOfCombat(player)) return;

        player.sendMessage(ChatColor.RED + "Non puoi eseguire comandi in combattimento!");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player damager = (Player) event.getDamager(), damaged = (Player) event.getEntity();

        if (damager.getLocation().getY() >= wc.getPvpDisabledOver() || damaged.getLocation().getY() >= wc.getPvpDisabledOver()) {
            event.setCancelled(true);
            return;
        }

        long time = System.currentTimeMillis();
        cc.getCombatTime().replace(damager, time);
        cc.getCombatTime().replace(damaged, time);

        cc.getLastHitters().replace(damaged, damager);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();
        BPlayer bVictim = playerBank.getBPlayerFrom(player);
        bVictim.addDeaths(1);
        bVictim.setEdited(true);

        player.getWorld().strikeLightningEffect(player.getLocation());

        event.setDeathMessage(null);
        event.getDrops().clear();

        if (cc.isOutOfCombat(player) || cc.getLastHitters().get(player) == null) return;

        Player killer = cc.getLastHitters().get(player);
        BPlayer bKiller = playerBank.getBPlayerFrom(killer);
        bKiller.addKills(1);
        bKiller.setEdited(true);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!playerBank.getBPlayerFrom(onlinePlayer).isNoDeaths() && player == onlinePlayer) continue;
            onlinePlayer.sendMessage(ChatColor.RED + player.getName() + " e' stato ucciso da " +
                    killer.getName());
        }

        instance.getServer().getPluginManager().callEvent(new CombatKillEvent(player, killer));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (cc.isOutOfCombat(player)) {
            return;
        }

        player.setHealth(0D);
    }

}
