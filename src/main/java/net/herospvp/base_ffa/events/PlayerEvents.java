package net.herospvp.base_ffa.events;

import net.herospvp.base_ffa.Main;
import net.herospvp.base_ffa.database.RAM;
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

        RAM.getOnlinePlayerNames().add(player.getName());

        player.getActivePotionEffects().clear();

        Main.getKit().setPlayerArmor(player);
        Main.getKit().setPlayerHotBar(player);
        Main.getKit().assignPotionEffects(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerQuitEvent event) {
        RAM.getOnlinePlayerNames().remove(event.getPlayer().getName());
    }

    @EventHandler
    public void on(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Main.getKit().setPlayerHotBar(player);
        Main.getKit().setPlayerArmor(player);
        Main.getKit().assignPotionEffects(player);
    }

}
