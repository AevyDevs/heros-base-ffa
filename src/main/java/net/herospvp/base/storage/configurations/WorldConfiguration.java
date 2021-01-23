package net.herospvp.base.storage.configurations;

import lombok.Getter;
import lombok.Setter;
import net.herospvp.base.Base;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WorldConfiguration {

    private final Base instance;
    @Getter
    private Location spawnPoint;
    @Getter
    private final World[] worlds;
    private int counter = 0;
    @Getter @Setter
    private int repeatEvery, pvpDisabledOver;
    @Getter
    private long timeRemaining;

    public WorldConfiguration(Base instance, String[] strings, int repeatEvery, int pvpDisabledOver) {

        this.instance = instance;
        this.repeatEvery = repeatEvery;
        this.pvpDisabledOver = pvpDisabledOver;

        this.worlds = new World[strings.length];
        for (int i = 0; i < strings.length; i++) {
            worlds[i] = Bukkit.getWorld(strings[i]);
        }
        spawnPoint = worlds[0].getSpawnLocation();

        if (worlds.length == 1) {
            timeRemaining = 0;
            return;
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, () -> {
            timeRemaining = timeRemaining == 0 ? repeatEvery : timeRemaining - 1000;

            if (timeRemaining <= 3000 && timeRemaining != 0) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.RED + "Cambio mappa in: " + timeRemaining / 1000 + "s");
                }
            } else if (timeRemaining == 0) {
                counter = counter == worlds.length - 1 ? 0 : counter + 1;

                spawnPoint = worlds[counter].getSpawnLocation();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(spawnPoint);
                }
            }

        }, 0L, 20L);

    }

}
