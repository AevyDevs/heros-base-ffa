package net.herospvp.base_ffa.commands;

import net.herospvp.base_ffa.Memory;
import net.herospvp.base_ffa.configuration.WorldConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = ((Player) commandSender).getPlayer();
        player.teleport(WorldConfiguration.getSpawnPoint());

        player.getInventory().clear();
        Memory.getKit().setPlayerArmor(player);
        Memory.getKit().setPlayerHotBar(player);

        return false;
    }

}
