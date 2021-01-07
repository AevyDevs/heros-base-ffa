package net.herospvp.base_ffa.utils;

import org.bukkit.ChatColor;

public class StringFormat {

    public static String forMysql(String string) {
        return "\"" + string + "\"";
    }

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
