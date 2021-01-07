package net.herospvp.base_ffa.commands;

import net.herospvp.base_ffa.database.RAM;
import net.herospvp.base_ffa.utils.StringFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reply implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = ((Player) commandSender).getPlayer();

        if (strings.length == 0) {
            player.sendMessage(ChatColor.RED + "Utilizza /r <messaggio>");
            return false;
        }

        if (RAM.getLastMessageFrom().get(player) == null) {
            player.sendMessage(ChatColor.RED + "Non hai nessun messaggio recente a cui rispondere!");
            return false;
        }

        Player target = RAM.getLastMessageFrom().get(player);

        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string);
        }

        target.sendMessage(StringFormat.translate("&6(/r) DA: " + player.getName() + " &7» &o"
                + stringBuilder.toString()));

        player.sendMessage(StringFormat.translate("&6(/r) A: " + target.getName() + " &7» &o"
                + stringBuilder.toString()));

        return false;
    }

}
