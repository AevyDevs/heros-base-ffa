package net.herospvp.base_ffa.tasks;

import lombok.Getter;
import net.herospvp.base_ffa.Main;
import net.herospvp.base_ffa.Memory;
import org.bukkit.Bukkit;

public class SaveData {

    @Getter
    private final int repeatEvery;

    public SaveData(int repeatEvery) {
        this.repeatEvery = repeatEvery;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getMain(), () -> {
            Memory.getMusician().updateMirror(Memory.saveAll());
            Memory.getMusician().play();
        }, repeatEvery, repeatEvery);
    }

}
