package net.herospvp.base_ffa.tasks;

import lombok.Getter;
import net.herospvp.base_ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class AlwaysDay {

    @Getter
    private final World world;
    @Getter
    private final int timeToSet, repeatEvery;

    public AlwaysDay(String world, int timeToSet, int repeatEvery) {
        this.world = Bukkit.getWorld(world);
        this.timeToSet = timeToSet;
        this.repeatEvery = repeatEvery;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getMain(), () -> {
            this.world.setTime(timeToSet);
        }, 0L, repeatEvery);
    }

}
