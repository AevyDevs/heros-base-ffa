package net.herospvp.base_ffa.configuration;

import lombok.Getter;
import net.herospvp.base_ffa.Main;
import net.herospvp.base_ffa.Memory;
import net.herospvp.base_ffa.database.Hikari;
import org.bukkit.Bukkit;

public class DatabaseConfiguration {

    @Getter
    private final String ip, port, user, database, table, password;

    public DatabaseConfiguration(String ip, String port, String user, String database, String table, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.database = database;
        this.table = table;
        this.password = password;

        Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> {
            try {
                Memory.setHikari(new Hikari(ip, port, database, table, user, password));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}
