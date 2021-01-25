package net.herospvp.base.events.tasks;

import lombok.Getter;
import net.herospvp.base.Base;
import net.herospvp.base.storage.Bank;
import net.herospvp.database.Musician;
import org.bukkit.Bukkit;

public class SaveData {

    private final Base instance;
    private final Musician tasksMusician;
    private final Bank bank;
    @Getter
    private final int repeatEvery;

    public SaveData(Base instance, int repeatEvery) {
        this.instance = instance;
        this.tasksMusician = instance.getTasksMusician();
        this.bank = instance.getBank();
        this.repeatEvery = repeatEvery;

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {

            tasksMusician.update(bank.save(false));
            tasksMusician.play();

        }, repeatEvery, repeatEvery);
    }


}
