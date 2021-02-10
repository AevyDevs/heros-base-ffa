package net.herospvp.base.commands;

import net.herospvp.base.Base;
import net.herospvp.base.storage.BPlayer;
import net.herospvp.base.storage.PlayerBank;
import net.herospvp.base.utils.StringFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reply implements CommandExecutor {

    private final StringFormat stringFormat;
    private final PlayerBank playerBank;

    public Reply(Base instance) {
        this.stringFormat = instance.getStringFormat();
        this.playerBank = instance.getPlayerBank();
        instance.getServer().getPluginCommand("reply").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = ((Player) commandSender).getPlayer();
        BPlayer bPlayer = playerBank.getBPlayerFrom(player);

        if (strings.length == 0) {
            player.sendMessage(ChatColor.RED + "Utilizza /r <messaggio>");
            return false;
        }

        if (playerBank.getLastMessages().get(bPlayer) == null) {
            player.sendMessage(ChatColor.RED + "Non hai nessun messaggio recente a cui rispondere!");
            return false;
        }

        BPlayer bTarget = playerBank.getLastMessages().get(bPlayer);
        Player target = bTarget.getPlayer();

        if (!bTarget.isNoMsg()) {
            player.sendMessage(ChatColor.RED + "Mi spiace, " + target.getName() + " ignora i messaggi privati!");
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string).append(" ");
        }

        target.sendMessage(stringFormat.translate("&6(/r) DA: " + player.getName() + " &7» &o"
                + stringBuilder.toString()));

        player.sendMessage(stringFormat.translate("&6(/r) A: " + target.getName() + " &7» &o"
                + stringBuilder.toString()));

        return false;
    }

}
