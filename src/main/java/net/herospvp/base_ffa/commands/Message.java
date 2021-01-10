package net.herospvp.base_ffa.commands;

import net.herospvp.base_ffa.database.RAM;
import net.herospvp.base_ffa.utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = ((Player) commandSender).getPlayer();

        if (!(strings.length >= 2)) {
            player.sendMessage(ChatColor.RED + "Utilizza /message <player> <messaggio>");
            return false;
        }

        if (!RAM.getOnlinePlayerNames().contains(strings[0])) {
            player.sendMessage(ChatColor.RED + strings[0] + " non e' online!");
            return false;
        }

        Player target = Bukkit.getPlayer(strings[0]);

        if (RAM.wantsNoMsg(target)) {
            player.sendMessage(ChatColor.RED + "Mi spiace, " + target.getName() + " ignora i messaggi privati!");
            return false;
        }

        if (target.equals(player)) {
            player.sendMessage(ChatColor.RED + "Vuoi mandare un messaggio a te stesso?");
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) continue;
            stringBuilder.append(strings[i]);
        }

        target.sendMessage(StringFormat.translate("&6(/m) DA: " + player.getName() + " &7» &o"
                + stringBuilder.toString()));

        player.sendMessage(StringFormat.translate("&6(/m) A: " + target.getName() + " &7» &o"
                + stringBuilder.toString()));

        RAM.getLastMessageFrom().replace(target, player);

        return false;
    }

}
