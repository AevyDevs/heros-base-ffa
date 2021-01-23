package net.herospvp.base;

import lombok.Getter;
import lombok.Setter;
import net.herospvp.base.commands.*;
import net.herospvp.base.events.CombatEvents;
import net.herospvp.base.events.PlayerEvents;
import net.herospvp.base.events.WorldEvents;
import net.herospvp.base.events.tasks.ActionbarAnnouncer;
import net.herospvp.base.events.tasks.AlwaysDay;
import net.herospvp.base.events.tasks.SaveData;
import net.herospvp.base.extensions.PlaceholderAPI;
import net.herospvp.base.storage.Bank;
import net.herospvp.base.storage.configurations.CombatConfigurations;
import net.herospvp.base.storage.configurations.WorldConfiguration;
import net.herospvp.base.utils.StringFormat;
import net.herospvp.database.Director;
import net.herospvp.database.Musician;
import net.herospvp.database.items.Instrument;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Base extends JavaPlugin {

    @Getter
    private static Base instance;
    @Getter
    private Chat chat;
    @Getter
    private StringFormat stringFormat;
    @Getter
    private Director director;
    @Getter
    private Musician tasksMusician, startStopMusician;
    @Getter
    private String table;
    @Getter
    private String[] fieldsOfTable, tableCreateFields;
    @Getter
    private Bank bank;
    @Getter
    private PlaceholderAPI placeholderAPI;
    @Getter
    private WorldConfiguration worldConfiguration;
    @Getter
    private CombatConfigurations combatConfigurations;
    @Getter
    private Spawn spawn;
    @Getter
    private CombatEvents combatEvents;
    @Getter
    private PlayerEvents playerEvents;
    @Getter
    private String serverVersion;
    @Getter
    private Sound sound;
    @Getter @Setter
    private boolean loaded;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        //
        // START DATABASE RELATED
        //

        table = getConfigString("mysql.table");
        fieldsOfTable = getConfigList("mysql.table_fields").toArray(new String[0]);
        tableCreateFields = getConfigList("mysql.table_create_fields").toArray(new String[0]);

        // creating a new director (so it can store instruments)
        director = new Director();

        Map<String, String> map = new HashMap<>();
        map.put("cacheCallableStmts", "true");
        map.put("metadataCacheSize", "152");
        map.put("maintainTimeStats", "false");
        map.put("rewriteBatchedStatements", "true");
        map.put("ha.loadBalanceStrategy", "bestResponseTime");
        map.put("useServerPrepStmts", "true");
        map.put("holdResultsOpenOverStatementClose", "true");
        //map.put("useCompression", "true");
        map.put("tcpKeepAlive", "false");

        // creating a new instrument (mysql connection to an ip:port -> database)
        Instrument guitar = new Instrument(getConfigString("mysql.ip"), getConfigString("mysql.port"),
                getConfigString("mysql.user"), getConfigString("mysql.password"), getConfigString("mysql.database"),
                "?useSSL=false&characterEncoding=utf8", map, true, 2);

        // giving the instrument to the director with a name
        director.addInstrument("guitar", guitar);

        // load string formatter
        stringFormat = new StringFormat(this);

        // creating a new musician and giving the instrument
        // this counts as a new Thread(), this musician can only play() this instrument (so it can only use
        // one mysql connection)
        startStopMusician = new Musician(director, guitar, true);

        // creating a second musician, this allows to not have any errors if a double play() is called
        tasksMusician = new Musician(director, guitar, true);

        // creating a new Bank so it can store every bit of information needed for this core
        bank = new Bank(this);

        // updating the mirror in the Musician-Thread so it can execute the lambdas later on
        startStopMusician.update(bank.init());

        // executing the lambdas to init Bank.class datastructures
        startStopMusician.play();

        //
        // END DATABASE RELATED
        //

        //
        // START REFLECTIONS FOR SOUNDS
        //

        String string = getServer().getClass().getPackage().getName();
        String[] split = string.split("\\.");

        if (!(split.length > 0)) {
            getLogger().severe("An error occured while loading Reflections!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        String versionSuffix = split[split.length - 1];

        if (!versionSuffix.startsWith("v")) {
            getLogger().severe("An error occured while loading Reflections! (Could not find version suffix)");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Reflections loaded. Version: " + versionSuffix);
        serverVersion = versionSuffix;

        try {
            if (instance.getServerVersion().contains("1_8")) {
                Class aClass = Class.forName("org.bukkit.Sound");
                int i = 0;
                for (Object soundEnum : aClass.getEnumConstants()) {
                    if (soundEnum.toString().contains("CAT_MEOW")) {
                        break;
                    }
                    i++;
                }
                sound = (Sound) aClass.getEnumConstants()[i];
            } else {
                sound = Sound.ENTITY_CAT_AMBIENT;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        // END REFLECTIONS FOR SOUNDS
        //

        //
        // START EVENTS AND EXPANSIONS RELATED
        //

        // register Vault chat grab
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();

        // loading combat-tag datastructures
        combatConfigurations = new CombatConfigurations(this, getConfigLong("combat_duration"));

        // getting and setting up worlds with optional multiple spawn-points
        worldConfiguration = new WorldConfiguration(this, getConfigArray("worlds.list"),
                getConfigInt("worlds.change_every"), 50);

        // loading events
        // the lambda here can be replaced on-the-fly, so it can do more operations such as giving a kit etc.
        playerEvents = new PlayerEvents(this, (player) -> {});
        new WorldEvents(this);
        // the lambda here can be replaced on-the-fly, so it can do more operations such as giving a kit etc.
        combatEvents = new CombatEvents(this, (player, killer) -> {});

        // load placeholders
        placeholderAPI = new PlaceholderAPI(this);
        placeholderAPI.addStats("kills", playerName -> String.valueOf(bank.getKills(playerName)));
        placeholderAPI.addStats("deaths", playerName -> String.valueOf(bank.getDeaths(playerName)));
        placeholderAPI.addStats("ks", playerName -> String.valueOf(bank.getStreak(playerName)));
        placeholderAPI.addStats("kd", playerName -> {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            long deaths = bank.getDeaths(playerName);
            return String.valueOf(decimalFormat.format(
                    (float) bank.getKills(playerName) / (float) (deaths == 0 ? 1 : deaths))
            );
        });
        placeholderAPI.addStats("map", playerName -> {
            Date date = new Date(worldConfiguration.getTimeRemaining());
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            return format.format(date);
        });

        //
        // END EVENTS AND EXPANSIONS RELATED
        //

        //
        // START COMMANDS AND TASKS RELATED
        //

        // the lambda here can be replaced on-the-fly, so it can do more operations such as giving a kit etc.
        spawn = new Spawn(this, (player) -> {});

        // loading commands
        new Reply(this);
        new Notifiche(this);
        new Message(this);
        new ForceSave(this);

        // loading tasks
        new AlwaysDay(this, getConfigInt("alwaysday.repeat_every"), getConfigInt("alwaysday.set_time"));
        new ActionbarAnnouncer(this, getConfigArray("actionbar.messages"),
                getConfigInt("actionbar.duration"), getConfigInt("actionbar.repeat_every"));
        new SaveData(this, getConfigInt("mysql.save_every"));

        //
        // END COMMANDS AND TASKS RELATED
        //
    }

    @Override
    public void onDisable() {
        // updating mirror for saving data
        startStopMusician.update(bank.save());
        // saving data
        startStopMusician.play();
        // gently shutdown threads
        director.endShow();
    }

    private String getConfigString(String string) {
        return getConfig().getString(string);
    }

    private int getConfigInt(String string) {
        return getConfig().getInt(string);
    }

    private long getConfigLong(String string) {
        return getConfig().getLong(string);
    }

    private boolean getConfigBoolean(String string) {
        return getConfig().getBoolean(string);
    }

    private List<String> getConfigList(String string) {
        return getConfig().getStringList(string);
    }

    private String[] getConfigArray(String string) {
        List<String> stringList = getConfig().getStringList(string);

        String[] strings = new String[stringList.size()];

        for (int i = 0; i < stringList.size(); i++) {
            strings[i] = stringList.get(i);
        }

        return strings;
    }
    
    
}
