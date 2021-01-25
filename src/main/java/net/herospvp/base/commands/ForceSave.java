package net.herospvp.base.commands;

import net.herospvp.base.Base;
import net.herospvp.base.storage.Bank;
import net.herospvp.database.Musician;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceSave implements CommandExecutor {

    private final Base instance;
    private final Bank bank;
    private final Musician musician;

    public ForceSave(Base instance) {
        this.instance = instance;
        this.bank = instance.getBank();
        this.musician = instance.getTasksMusician();
        instance.getServer().getPluginCommand("forcesave").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        musician.update(bank.save(false));
        musician.play();

        commandSender.sendMessage(ChatColor.RED + "[BaseFFA 2.2.3] Salvando...");

        return false;
    }

}
