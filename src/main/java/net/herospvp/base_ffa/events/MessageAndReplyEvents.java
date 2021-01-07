package net.herospvp.base_ffa.events;

import net.herospvp.base_ffa.database.RAM;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MessageAndReplyEvents implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent event) {
        RAM.getLastMessageFrom().put(event.getPlayer(), null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerQuitEvent event) {
        RAM.getLastMessageFrom().remove(event.getPlayer());
    }

}
