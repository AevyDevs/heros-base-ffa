package net.herospvp.base.commands;

import net.herospvp.base.Base;
import net.herospvp.base.storage.BPlayer;
import net.herospvp.base.storage.PlayerBank;
import net.herospvp.base.utils.StringFormat;
import net.herospvp.heroscore.utils.strings.message.Message;
import net.herospvp.heroscore.utils.strings.message.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {

    private final StringFormat stringFormat;
    private final PlayerBank playerBank;
    private static final String commandName = "Reply";

    public ReplyCommand(Base instance) {
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
            Message.sendMessage(commandSender, MessageType.ERROR, commandName,
                    "Utilizza /r <messaggio>");
            return false;
        }

        BPlayer bTarget = playerBank.getLastMessages().get(bPlayer);

        if (bTarget == null) {
            Message.sendMessage(commandSender, MessageType.ERROR, commandName,
                    "Non hai nessun messaggio recente a cui rispondere!");
            return false;
        }

        Player target = bTarget.getPlayer();

        if (!bTarget.isNoMsg()) {
            Message.sendMessage(commandSender, MessageType.ERROR, commandName,
                    "Mi spiace, " + target.getName() + " ignora i messaggi privati!");
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
