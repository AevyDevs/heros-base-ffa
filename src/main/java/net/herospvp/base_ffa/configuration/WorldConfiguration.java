package net.herospvp.base_ffa.configuration;

import lombok.Getter;
import lombok.Setter;
import net.herospvp.base_ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldConfiguration {

    @Getter
    private final World world;
    @Getter
    private static Location spawnPoint = null;
    @Getter
    private final World[] worlds;
    @Getter @Setter
    private static int repeatEvery = 60000;
    private static int counter = 0;
    @Getter @Setter
    private static int pvpDisabledOver = 0;

    public WorldConfiguration(String world, World[] worlds) {
        this.world = Bukkit.getWorld(world);
        this.worlds = worlds;
        spawnPoint = this.world.getSpawnLocation();
        if (worlds != null) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getMain(), () -> {

                spawnPoint = worlds[counter].getSpawnLocation();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(spawnPoint);
                }

                counter = counter == worlds.length ? 0 : counter++;

            }, 0L, repeatEvery);
        }
    }

}
