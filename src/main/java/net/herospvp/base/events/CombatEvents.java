package net.herospvp.base.events;

import lombok.Setter;
import net.herospvp.base.Base;
import net.herospvp.base.storage.Bank;
import net.herospvp.base.storage.configurations.CombatConfigurations;
import net.herospvp.base.storage.configurations.WorldConfiguration;
import net.herospvp.base.utils.lambdas.CombatEventsLambda;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatEvents {

    private final Base instance;
    private final Bank bank;
    private final CombatConfigurations cc;
    private final WorldConfiguration wc;
    @Setter
    private CombatEventsLambda combatEventsLambda;

    public CombatEvents(Base instance, CombatEventsLambda combatEventsLambda) {
        this.instance = instance;
        this.bank = instance.getBank();
        this.cc = instance.getCombatConfigurations();
        this.wc = instance.getWorldConfiguration();
        this.combatEventsLambda = combatEventsLambda;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (cc.isOutOfCombat(player)) {
            return;
        }

        player.sendMessage(ChatColor.RED + "Non puoi eseguire comandi in combattimento!");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player || event.getDamager() instanceof Player)) {
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

        bank.addDeaths(player, 1);

        player.getWorld().strikeLightningEffect(player.getLocation());

        event.setDeathMessage(null);
        event.getDrops().clear();

        if (cc.isOutOfCombat(player)) {
            return;
        }

        Player killer = cc.getLastHitters().get(player);

        if (killer == null) {
            return;
        }

        bank.addKills(killer, 1);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!bank.wantsDeaths(onlinePlayer) && player != onlinePlayer) continue;
            onlinePlayer.sendMessage(ChatColor.RED + player.getName() + " e' stato ucciso da " +
                    killer.getName());
        }

        combatEventsLambda.func(player, killer);
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
