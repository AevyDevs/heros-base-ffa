package net.herospvp.base_ffa.utils;

import net.herospvp.base_ffa.Memory;
import net.herospvp.base_ffa.database.RAM;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StringFormat {

    public static String forMysql(String string) {
        return "\"" + string + "\"";
    }

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String colorChatString(Player player, ChatColor chatColor) {
        String playerName = player.getName();
        String prefix = Memory.getChat().getPlayerPrefix(player);
        prefix = translate(prefix);

        return translate("&7[&a" + RAM.getKills(player) + "&7] "
                + prefix + chatColor + playerName + " &7» %2$s");
    }

}
