package net.herospvp.base.events.tasks;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import lombok.Getter;
import net.herospvp.base.Base;
import net.herospvp.base.utils.StringFormat;
import org.bukkit.Bukkit;

public class ActionbarAnnouncer {

    private final StringFormat stringFormat;
    @Getter
    private final String[] messages;
    @Getter
    private final int duration, repeatEvery;
    private int counter = 0;

    public ActionbarAnnouncer(Base instance, String[] messages, int duration, int repeatEvery) {
        this.stringFormat = instance.getStringFormat();
        this.messages = messages;
        this.duration = duration;
        this.repeatEvery = repeatEvery;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, () -> {

            ActionBarAPI.sendActionBarToAllPlayers(stringFormat.translate(messages[counter]), duration);

            counter = counter == messages.length - 1 ? 0 : counter + 1;

        }, 0L, repeatEvery);
    }

}
