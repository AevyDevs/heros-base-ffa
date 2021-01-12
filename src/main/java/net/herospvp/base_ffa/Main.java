package net.herospvp.base_ffa;

import lombok.Getter;
import net.herospvp.base_ffa.commands.Message;
import net.herospvp.base_ffa.commands.Notifiche;
import net.herospvp.base_ffa.commands.Reply;
import net.herospvp.base_ffa.commands.Spawn;
import net.herospvp.base_ffa.database.extensions.PAPI;
import net.herospvp.base_ffa.events.CombatTagEvents;
import net.herospvp.base_ffa.events.PlayerEvents;
import net.herospvp.base_ffa.events.WorldEvents;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    private static Main main;

    @Override
    public void onEnable() {
        main = this;

        new PAPI();

        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        Memory.setChat(rsp.getProvider());

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
        Memory.getMusician().updateMirror(Memory.saveAll());
        Memory.getMusician().play();
    }

}
