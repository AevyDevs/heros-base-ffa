package net.herospvp.base.storage.configurations;

import lombok.Getter;
import net.herospvp.base.Base;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CombatConfigurations {

    private final long duration;
    private final Map<Player, Long> combatTime;
    private final Map<Player, Player> lastHitters;

    public CombatConfigurations(long duration) {
        this.lastHitters = new HashMap<>();
        this.combatTime = new HashMap<>();
        this.duration = duration;
    }

    public boolean isOutOfCombat(Player player) {
        return ((combatTime.get(player) + duration) < System.currentTimeMillis());
    }

}
