package net.herospvp.base.commands;

import net.herospvp.base.Base;
import net.herospvp.base.events.custom.SpawnEvent;
import net.herospvp.base.utils.StringFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final Base instance;

    public SpawnCommand(Base instance) {
        this.instance = instance;
        instance.getServer().getPluginCommand("spawn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = ((Player) commandSender).getPlayer();
        player.teleport(instance.getWorldConfiguration().getSpawnPoint());

        instance.getServer().getPluginManager().callEvent(new SpawnEvent(player));

        return false;
    }

}
