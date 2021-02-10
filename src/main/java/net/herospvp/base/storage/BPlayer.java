package net.herospvp.base.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.herospvp.heroscore.objects.HPlayer;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class BPlayer {

    private final HPlayer hPlayer;
    private final Player player;

    private int kills, deaths, streak;
    private boolean noDeaths, noPings, noMsg;
    @Setter
    private boolean edited;

    public void addKills(int i) {
        kills += i;
    }

    public void removeKills(int i) {
        kills -= i;
    }

    public void addDeaths(int i) {
        deaths += i;
    }

    public void removeDeaths(int i) {
        deaths -= i;
    }

    public void addStreak(int i) {
        streak += i;
    }

    public void removeStreak(int i) {
        streak -= i;
    }

    public void changeMsgIdea() {
        noMsg = !noMsg;
    }

    public void changeDeathsIdea() {
        noDeaths = !noDeaths;
    }

    public void changePingsIdea() {
        noPings = !noPings;
    }

}
