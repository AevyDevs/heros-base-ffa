package net.herospvp.base.commands;

import net.herospvp.base.Base;
import net.herospvp.base.storage.Bank;
import net.herospvp.base.utils.StringFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Notifiche implements CommandExecutor {

    private final Base instance;
    private final StringFormat stringFormat;
    private final Bank bank;

    public Notifiche(Base instance) {
        this.instance = instance;
        this.stringFormat = instance.getStringFormat();
        this.bank = instance.getBank();
        instance.getServer().getPluginCommand("notifiche").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = ((Player) commandSender).getPlayer();

        if (strings.length != 1) {
            player.sendMessage(stringFormat.translate("\n&cLe tue notifiche:"));
            player.sendMessage(stringFormat.translate("&81 » &7Messaggi privati: "
                    + (!bank.wantsMsg(player) ? "&cOFF" : "&aON")));
            player.sendMessage(stringFormat.translate("&82 » &7Messaggi di morte: "
                    + (!bank.wantsDeaths(player) ? "&cOFF" : "&aON")));
            player.sendMessage(stringFormat.translate("&83 » &7Menzioni in chat: "
                    + (!bank.wantsPings(player) ? "&cOFF" : "&aON") + "\n"));

            player.sendMessage(stringFormat.translate("\n&cGestisci le tue notifiche con /notifiche <1/2/3>"));
        } else {
            switch (strings[0]) {
                case "1":
                    bank.changeMsgIdea(player);
                    break;
                case "2":
                    bank.changeDeathsIdea(player);
                    break;
                case "3":
                    bank.changePingsIdea(player);
                    break;
                default:
                    player.sendMessage(stringFormat.translate("\n&cGestisci le tue notifiche con /notifiche <1/2/3>"));
                    return false;
            }

            player.sendMessage(stringFormat.translate("\n&cLe tue notifiche:"));
            player.sendMessage(stringFormat.translate("&81 » &7Messaggi privati: "
                    + (!bank.wantsMsg(player) ? "&cOFF" : "&aON")));
            player.sendMessage(stringFormat.translate("&82 » &7Messaggi di morte: "
                    + (!bank.wantsDeaths(player) ? "&cOFF" : "&aON")));
            player.sendMessage(stringFormat.translate("&83 » &7Menzioni in chat: "
                    + (!bank.wantsPings(player) ? "&cOFF" : "&aON") + "\n"));
        }

        return false;
    }

}
