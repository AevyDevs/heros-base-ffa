package net.herospvp.base.events.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class MapChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final World newWorld, oldWorld;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}