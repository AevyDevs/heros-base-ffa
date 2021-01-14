package net.herospvp.base.commands;

import lombok.Getter;
import lombok.Setter;
import net.herospvp.base.Base;
import net.herospvp.base.storage.Bank;
import net.herospvp.base.utils.StringFormat;
import net.herospvp.base.utils.lambdas.SpawnLambda;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn implements CommandExecutor {

    private final Base instance;
    private final StringFormat stringFormat;
    private final Bank bank;
    @Getter @Setter
    private SpawnLambda spawnLambda;

    public Spawn(Base instance, SpawnLambda spawnLambda) {
        this.instance = instance;
        this.spawnLambda = spawnLambda;
        this.stringFormat = instance.getStringFormat();
        this.bank = instance.getBank();
        instance.getServer().getPluginCommand("spawn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = ((Player) commandSender).getPlayer();
        player.teleport(instance.getWorldConfiguration().getSpawnPoint());

        spawnLambda.func(player);

        return false;
    }

}
