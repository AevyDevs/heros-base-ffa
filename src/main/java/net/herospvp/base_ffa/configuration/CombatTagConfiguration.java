package net.herospvp.base_ffa.configuration;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CombatTagConfiguration {

    @Getter @Setter
    private static long duration = 6000;
    @Getter
    private static final Map<Player, Long> mapOfPlayersInCombat = new HashMap<>();
    @Getter
    private static final Map<Player, Player> lastHitter = new HashMap<>();

    public static boolean isOutOfCombat(Player player) {
        return ((mapOfPlayersInCombat.get(player) + duration) < System.currentTimeMillis());
    }

}
