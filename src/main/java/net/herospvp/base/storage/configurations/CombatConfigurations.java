package net.herospvp.base.storage.configurations;

import lombok.Getter;
import net.herospvp.base.Base;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CombatConfigurations {

    private final Base instance;
    @Getter
    private final long duration;
    @Getter
    private final Map<Player, Long> combatTime;
    @Getter
    private final Map<Player, Player> lastHitters;

    public CombatConfigurations(Base instance, long duration) {
        this.instance = instance;
        this.lastHitters = new HashMap<>();
        this.combatTime = new HashMap<>();
        this.duration = duration;
    }

    public boolean isOutOfCombat(Player player) {
        return ((combatTime.get(player) + duration) < System.currentTimeMillis());
    }

}
