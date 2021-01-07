package net.herospvp.base_ffa.database;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class RAM {

    @Getter
    private static final Map<Player, Integer[]> storedPlayers = new HashMap<>();
    // 0 = kills; 1 = deaths; 2 = streak; 3 = noDeaths; 4 = noPings; 5 = noMsg
    @Getter
    private static final TreeSet<String> onlinePlayerNames = new TreeSet<>();
    @Getter
    private static final Map<Player, Player> lastMessageFrom = new HashMap<>();

    public static void setEveryData(Player player, Integer[] data) {
        storedPlayers.put(player, data);
    }

    public static int getKills(Player player) {
        return storedPlayers.get(player)[0];
    }

    public static void addKills(Player player, int kills) {
        addStreak(player, 1);
        storedPlayers.get(player)[0] += kills;
    }

    public static int getDeaths(Player player) {
        return storedPlayers.get(player)[1];
    }

    public static void addDeaths(Player player, int deaths) {
        storedPlayers.get(player)[1] += deaths;
    }

    public static int getStreak(Player player) {
        return storedPlayers.get(player)[2];
    }

    public static void addStreak(Player player, int streak) {
        storedPlayers.get(player)[2] += streak;
    }

    public static boolean wantsNoDeaths(Player player) {
        return storedPlayers.get(player)[3] == 1;
    }

    public static void changeDeathsIdea(Player player) {
        storedPlayers.get(player)[3] = storedPlayers.get(player)[3] == 1 ? 0 : 1;
    }

    public static boolean wantsNoPings(Player player) {
        return storedPlayers.get(player)[4] == 1;
    }

    public static void changePingsIdea(Player player) {
        storedPlayers.get(player)[4] = storedPlayers.get(player)[3] == 1 ? 0 : 1;
    }

    public static boolean wantsNoMsg(Player player) {
        return storedPlayers.get(player)[5] == 1;
    }

    public static void changeMsgIdea(Player player) {
        storedPlayers.get(player)[5] = storedPlayers.get(player)[5] == 1 ? 0 : 1;
    }

}
