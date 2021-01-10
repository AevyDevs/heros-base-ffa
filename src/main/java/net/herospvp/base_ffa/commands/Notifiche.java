package net.herospvp.base_ffa.commands;

import net.herospvp.base_ffa.database.RAM;
import net.herospvp.base_ffa.utils.StringFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Notifiche implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = ((Player) commandSender).getPlayer();

        if (strings.length != 1) {
            player.sendMessage(StringFormat.translate("\n&cLe tue notifiche:"));
            player.sendMessage(StringFormat.translate("&81 » &7Messaggi privati: "
                    + (RAM.wantsNoMsg(player) ? "&cOFF" : "&aON")));
            player.sendMessage(StringFormat.translate("&82 » &7Messaggi di morte: "
                    + (RAM.wantsNoDeaths(player) ? "&cOFF" : "&aON")));
            player.sendMessage(StringFormat.translate("&83 » &7Menzioni in chat: "
                    + (RAM.wantsNoPings(player) ? "&cOFF" : "&aON") + "\n"));


            player.sendMessage(StringFormat.translate("\n&cGestisci le tue notifiche con /notifiche <1/2/3>"));
        } else {
            switch (strings[0]) {
                case "1":
                    RAM.changeMsgIdea(player);
                    break;
                case "2":
                    RAM.changeDeathsIdea(player);
                    break;
                case "3":
                    RAM.changePingsIdea(player);
                    break;
                default:
                    player.sendMessage(StringFormat.translate("\n&cGestisci le tue notifiche con /notifiche <1/2/3>"));
                    return false;
            }

            player.sendMessage(StringFormat.translate("\n&cLe tue notifiche:"));
            player.sendMessage(StringFormat.translate("&81 » &7Messaggi privati: "
                    + (RAM.wantsNoMsg(player) ? "&cOFF" : "&aON")));
            player.sendMessage(StringFormat.translate("&82 » &7Messaggi di morte: "
                    + (RAM.wantsNoDeaths(player) ? "&cOFF" : "&aON")));
            player.sendMessage(StringFormat.translate("&83 » &7Menzioni in chat: "
                    + (RAM.wantsNoPings(player) ? "&cOFF" : "&aON") + "\n"));
        }



        return false;
    }
}
