package net.herospvp.base.utils;

import net.herospvp.base.Base;
import net.herospvp.base.storage.BPlayer;
import net.herospvp.base.storage.PlayerBank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StringFormat {

    private final Base instance;
    private final PlayerBank playerBank;

    public StringFormat(Base instance) {
        this.instance = instance;
        this.playerBank = instance.getPlayerBank();
    }

    public String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public String color(BPlayer bPlayer, Player player, ChatColor chatColor) {
        return translate("&7[&a" + bPlayer.getKills() + "&7] " +
                instance.getChat().getPlayerPrefix(player) + chatColor + player.getName() + " &7» %2$s");
    }

}
