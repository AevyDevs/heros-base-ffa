package net.herospvp.base.events.tasks;

import lombok.Getter;
import net.herospvp.base.Base;
import net.herospvp.base.storage.configurations.WorldConfiguration;
import org.bukkit.Bukkit;

public class AlwaysDay {

    private final Base instance;
    @Getter
    private final int repeatEvery, setTime;
    private final WorldConfiguration worldConfiguration;

    public AlwaysDay(Base instance, int repeatEvery, int setTime) {
        this.instance = instance;
        this.repeatEvery = repeatEvery;
        this.setTime = setTime;
        this.worldConfiguration = instance.getWorldConfiguration();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, () -> {

            for (int i = 0; i < worldConfiguration.getWorlds().length; i++) {
                worldConfiguration.getWorlds()[i].setTime(setTime);
            }

        }, 0L, repeatEvery);
    }

}
