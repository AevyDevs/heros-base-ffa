package net.herospvp.base_ffa.commands;

import net.herospvp.base_ffa.Main;
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
        Main.getKit().setPlayerArmor(player);
        Main.getKit().setPlayerHotBar(player);

        return false;
    }

}
