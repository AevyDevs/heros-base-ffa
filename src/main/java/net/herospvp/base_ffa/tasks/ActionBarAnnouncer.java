package net.herospvp.base_ffa.tasks;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import lombok.Getter;
import net.herospvp.base_ffa.Main;
import org.bukkit.Bukkit;

public class ActionBarAnnouncer {

    @Getter
    private final String[] messages;
    @Getter
    private final int duration, repeatEvery;
    private static int counter = 0;

    public ActionBarAnnouncer(String[] messages, int duration, int repeatEvery) {
        this.messages = messages;
        this.duration = duration;
        this.repeatEvery = repeatEvery;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getMain(), () -> {

            ActionBarAPI.sendActionBarToAllPlayers(messages[counter], duration);

            counter = counter == messages.length ? 0 : counter++;

        }, 0L, repeatEvery);
    }

}
