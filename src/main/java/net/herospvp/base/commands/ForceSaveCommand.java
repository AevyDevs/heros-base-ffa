package net.herospvp.base.commands;

import net.herospvp.base.Base;
import net.herospvp.base.storage.PlayerBank;
import net.herospvp.database.Musician;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceSaveCommand implements CommandExecutor {

    private final PlayerBank playerBank;
    private final Musician musician;

    public ForceSaveCommand(Base instance) {
        this.playerBank = instance.getPlayerBank();
        this.musician = instance.getMusician();
        instance.getServer().getPluginCommand("forcesave").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        playerBank.saveAll();
        musician.play();

        commandSender.sendMessage(ChatColor.RED + "[BaseFFA 2.3.0] Salvando...");

        return false;
    }

}
