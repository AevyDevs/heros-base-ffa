package net.herospvp.base_ffa.events;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldEvents implements Listener {

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void on(ThunderChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void on(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)
                && event.getPlayer().hasPermission("*")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)
                && event.getPlayer().hasPermission("*")) return;
        event.setCancelled(true);
    }

}
