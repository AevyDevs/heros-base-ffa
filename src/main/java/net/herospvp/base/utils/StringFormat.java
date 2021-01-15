package net.herospvp.base.utils;

import net.herospvp.base.Base;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StringFormat {

    private final Base instance;

    public StringFormat(Base instance) {
        this.instance = instance;
    }

    public String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public String color(Player player, ChatColor chatColor) {
        String playerName = player.getName();
        String prefix = translate(instance.getChat().getPlayerPrefix(player));

        return translate("&7[&a" + instance.getBank().getKills(playerName) + "&7] "
                + prefix + chatColor + playerName + " &7» %2$s");
    }

    public String convert(Object object) {
        String string = null;
        if (object instanceof Player) {
            string = ((Player) object).getName();
        } else if (object instanceof String) {
            string = (String) object;
        }
        return string;
    }

}
