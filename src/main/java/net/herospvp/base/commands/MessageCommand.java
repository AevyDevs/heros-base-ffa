package net.herospvp.base.commands;

import net.herospvp.base.Base;
import net.herospvp.base.storage.BPlayer;
import net.herospvp.base.storage.PlayerBank;
import net.herospvp.base.utils.StringFormat;
import net.herospvp.heroscore.utils.strings.message.Message;
import net.herospvp.heroscore.utils.strings.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand implements CommandExecutor {

    private final StringFormat stringFormat;
    private final PlayerBank playerBank;
    private static final String commandName = "Message";

    public MessageCommand(Base instance) {
        this.stringFormat = instance.getStringFormat();
        this.playerBank = instance.getPlayerBank();
        instance.getServer().getPluginCommand("message").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = ((Player) commandSender).getPlayer();
        BPlayer bPlayer = playerBank.getBPlayerFrom(player);

        if (!(strings.length >= 2)) {
            Message.sendMessage(commandSender, MessageType.ERROR, commandName,
                    "Utilizza /message <player> <messaggio>");
            return false;
        }

        BPlayer bTarget = playerBank.getBPlayerFrom(strings[0]);

        if (bTarget == null || !playerBank.getBPlayers().contains(bTarget)) {
            Message.sendMessage(commandSender, MessageType.ERROR, commandName,
                    strings[0] + " non e' online!");
            return false;
        }

        Player target = Bukkit.getPlayer(strings[0]);

        if (!bTarget.isNoMsg()) {
            Message.sendMessage(commandSender, MessageType.ERROR, commandName,
                    "Mi spiace, " + target.getName() + " ignora i messaggi privati!");
            return false;
        }

        if (target.equals(player)) {
            Message.sendMessage(commandSender, MessageType.ERROR, commandName,
                    "Vuoi mandare un messaggio a te stesso?");
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) continue;
            stringBuilder.append(strings[i]).append(" ");
        }

        target.sendMessage(stringFormat.translate("&6(/m) DA: " + player.getName() + " &7» &o"
                + stringBuilder.toString()));

        player.sendMessage(stringFormat.translate("&6(/m) A: " + target.getName() + " &7» &o"
                + stringBuilder.toString()));

        playerBank.getLastMessages().replace(bTarget, bPlayer);
        return false;
    }

}
