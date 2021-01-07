package net.herospvp.base_ffa;

import lombok.Getter;
import lombok.Setter;
import net.herospvp.base_ffa.commands.Message;
import net.herospvp.base_ffa.commands.Notifiche;
import net.herospvp.base_ffa.commands.Reply;
import net.herospvp.base_ffa.commands.Spawn;
import net.herospvp.base_ffa.configuration.kit.Kit;
import net.herospvp.base_ffa.database.Hikari;
import net.herospvp.base_ffa.database.extensions.PAPI;
import net.herospvp.base_ffa.events.CombatTagEvents;
import net.herospvp.base_ffa.events.PlayerEvents;
import net.herospvp.base_ffa.events.WorldEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    private static Main main;
    @Getter @Setter
    private static Hikari hikari;
    @Getter @Setter
    private static Kit kit;

    @Override
    public void onEnable() {
        main = this;

        new PAPI();

        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new CombatTagEvents(), this);
        getServer().getPluginManager().registerEvents(new WorldEvents(), this);

        getServer().getPluginCommand("spawn").setExecutor(new Spawn());
        getServer().getPluginCommand("message").setExecutor(new Message());
        getServer().getPluginCommand("reply").setExecutor(new Reply());
        getServer().getPluginCommand("notifiche").setExecutor(new Notifiche());
    }

    @Override
    public void onDisable() {
        hikari.saveAll();
    }

}
