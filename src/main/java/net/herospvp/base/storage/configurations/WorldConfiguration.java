package net.herospvp.base.storage.configurations;

import lombok.Getter;
import lombok.Setter;
import net.herospvp.base.Base;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class WorldConfiguration {

    private final Base instance;
    @Getter
    private Location spawnPoint;
    @Getter
    private final World[] worlds;
    private int counter = 0;
    @Getter @Setter
    private int repeatEvery, pvpDisabledOver;

    public WorldConfiguration(Base instance, String[] strings, int repeatEvery, int pvpDisabledOver) {

        this.instance = instance;
        this.repeatEvery = repeatEvery;
        this.pvpDisabledOver = pvpDisabledOver;

        this.worlds = new World[strings.length];
        for (int i = 0; i < strings.length; i++) {
            worlds[i] = Bukkit.getWorld(strings[i]);
        }

        if (worlds.length == 1) {
            spawnPoint = worlds[0].getSpawnLocation();
            return;
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, () -> {

            spawnPoint = worlds[counter].getSpawnLocation();

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(spawnPoint);
            }

            counter = counter == worlds.length ? 0 : counter++;

        }, repeatEvery, repeatEvery);

    }

}
