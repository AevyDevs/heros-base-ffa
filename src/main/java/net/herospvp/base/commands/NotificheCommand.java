package net.herospvp.base.commands;

import net.herospvp.base.Base;
import net.herospvp.base.storage.BPlayer;
import net.herospvp.base.storage.PlayerBank;
import net.herospvp.base.utils.StringFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NotificheCommand implements CommandExecutor {

    private final StringFormat stringFormat;
    private final PlayerBank playerBank;

    public NotificheCommand(Base instance) {
        this.stringFormat = instance.getStringFormat();
        this.playerBank = instance.getPlayerBank();
        instance.getServer().getPluginCommand("notifiche").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = ((Player) commandSender).getPlayer();
        BPlayer bPlayer = playerBank.getBPlayerFrom(player);

        if (strings.length != 1) {
            player.sendMessage(stringFormat.translate("\n&cLe tue notifiche:"));
            player.sendMessage(stringFormat.translate("&81 » &7Messaggi privati: "
                    + (!bPlayer.isNoMsg() ? "&cOFF" : "&aON")));
            player.sendMessage(stringFormat.translate("&82 » &7Messaggi di morte: "
                    + (!bPlayer.isNoDeaths() ? "&cOFF" : "&aON")));
            player.sendMessage(stringFormat.translate("&83 » &7Menzioni in chat: "
                    + (!bPlayer.isNoPings() ? "&cOFF" : "&aON") + "\n"));

            player.sendMessage(stringFormat.translate("\n&cGestisci le tue notifiche con /notifiche <messaggi/morti/menzioni>"));
        } else {
            switch (strings[0]) {
                case "messaggi":
                    bPlayer.changeMsgIdea();
                    bPlayer.setEdited(true);
                    break;
                case "morti":
                    bPlayer.changeDeathsIdea();
                    bPlayer.setEdited(true);
                    break;
                case "menzioni":
                    bPlayer.changePingsIdea();
                    bPlayer.setEdited(true);
                    break;
                default:
                    player.sendMessage(stringFormat.translate("\n&cGestisci le tue notifiche con /notifiche <messaggi/morti/menzioni>"));
                    return false;
            }

            player.sendMessage(stringFormat.translate("\n&cLe tue notifiche:"));
            player.sendMessage(stringFormat.translate("&81 » &7Messaggi privati: "
                    + (!bPlayer.isNoMsg() ? "&cOFF" : "&aON")));
            player.sendMessage(stringFormat.translate("&82 » &7Messaggi di morte: "
                    + (!bPlayer.isNoDeaths() ? "&cOFF" : "&aON")));
            player.sendMessage(stringFormat.translate("&83 » &7Menzioni in chat: "
                    + (!bPlayer.isNoPings() ? "&cOFF" : "&aON") + "\n"));
        }

        return false;
    }

}
