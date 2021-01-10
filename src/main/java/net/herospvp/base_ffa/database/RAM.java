package net.herospvp.base_ffa.database;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class RAM {

    // 0 = kills; 1 = deaths; 2 = streak; 3 = noDeaths; 4 = noPings; 5 = noMsg
    @Getter
    private static final Map<String, Integer[]> storedPlayers = new HashMap<>();

    @Getter
    private static final TreeSet<String> onlinePlayerNames = new TreeSet<>();

    @Getter
    private static final Map<Player, Player> lastMessageFrom = new HashMap<>();

    public static void newEntry(Player player) {
        if (!RAM.getStoredPlayers().containsKey(player.getName())) {
            Integer[] integers = new Integer[6];
            Arrays.fill(integers, 0);
            storedPlayers.put(player.getName(), integers);
        }
    }

    public static void setEveryData(String playerName, Integer[] data) {
        storedPlayers.put(playerName, data);
    }

    public static int getKills(Player player) {
        return storedPlayers.get(player.getName())[0];
    }

    public static void addKills(Player player, int kills) {
        addStreak(player, 1);
        storedPlayers.get(player.getName())[0] += kills;
    }

    public static int getDeaths(Player player) {
        return storedPlayers.get(player.getName())[1];
    }

    public static void addDeaths(Player player, int deaths) {
        storedPlayers.get(player.getName())[2] = 0;
        storedPlayers.get(player.getName())[1] += deaths;
    }

    public static int getStreak(Player player) {
        return storedPlayers.get(player.getName())[2];
    }

    public static void addStreak(Player player, int streak) {
        storedPlayers.get(player.getName())[2] += streak;
    }

    public static boolean wantsNoDeaths(Player player) {
        return storedPlayers.get(player.getName())[3] == 1;
    }

    public static void changeDeathsIdea(Player player) {
        storedPlayers.get(player.getName())[3] = storedPlayers.get(player.getName())[3] == 1 ? 0 : 1;
    }

    public static boolean wantsNoPings(Player player) {
        return storedPlayers.get(player.getName())[4] == 1;
    }

    public static void changePingsIdea(Player player) {
        storedPlayers.get(player.getName())[4] = storedPlayers.get(player.getName())[4] == 1 ? 0 : 1;
    }

    public static boolean wantsNoMsg(Player player) {
        return storedPlayers.get(player.getName())[5] == 1;
    }

    public static void changeMsgIdea(Player player) {
        storedPlayers.get(player.getName())[5] = storedPlayers.get(player.getName())[5] == 1 ? 0 : 1;
    }

}
