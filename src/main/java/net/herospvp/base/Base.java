package net.herospvp.base;

import lombok.Getter;
import lombok.SneakyThrows;
import net.herospvp.base.commands.*;
import net.herospvp.base.events.CombatEvents;
import net.herospvp.base.events.PlayerEvents;
import net.herospvp.base.events.WorldEvents;
import net.herospvp.base.events.tasks.ActionbarAnnouncer;
import net.herospvp.base.events.tasks.AlwaysDay;
import net.herospvp.base.extensions.PlaceholderAPI;
import net.herospvp.base.storage.PlayerBank;
import net.herospvp.base.storage.configurations.CombatConfigurations;
import net.herospvp.base.storage.configurations.WorldConfiguration;
import net.herospvp.base.utils.StringFormat;
import net.herospvp.database.Director;
import net.herospvp.database.Musician;
import net.herospvp.database.items.Instrument;
import net.herospvp.heroscore.HerosCore;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Getter
public class Base extends JavaPlugin {

    private HerosCore herosCore;
    private Base instance;
    private Chat chat;
    private StringFormat stringFormat;
    private Director director;
    private Musician musician;
    private String table;
    private String[] fieldsOfTable, tableCreateFields;
    private PlayerBank playerBank;
    private PlaceholderAPI placeholderAPI;
    private WorldConfiguration worldConfiguration;
    private CombatConfigurations combatConfigurations;
    private String serverVersion;
    private Sound sound;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        herosCore = getPlugin(HerosCore.class);

        //
        // START DATABASE RELATED
        //

        table = getConfigString("db.table");
        fieldsOfTable = getConfigList("db.table_fields").toArray(new String[0]);
        tableCreateFields = getConfigList("db.table_create_fields").toArray(new String[0]);

        // creating a new instrument (mysql connection to an ip:port -> database)
        Instrument instrument = new Instrument(
                null, getConfigString("db.ip"),
                getConfigString("db.port"), getConfigString("db.database"), getConfigString("db.user"),
                getConfigString("db.password"), getConfigString("db.url"), getConfigString("db.driver"),
                null, true, 1
        );
        instrument.assemble();

        director = herosCore.getDirector();

        // giving the instrument to the director with a name
        director.addInstrument("base-ffa", instrument);

        // load string formatter
        stringFormat = new StringFormat(this);

        musician = herosCore.getMusician();

        // creating a new PlayerBank so it can store every bit of information needed for this core
        playerBank = new PlayerBank(this);

        // updating the mirror in the Musician-Thread so it can execute the lambdas later on
        musician.update(playerBank.startup());

        // executing the lambdas to init PlayerBank.class datastructures
        musician.play();

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

        // register Vault chat hook
        getServer().getServicesManager().load(Chat.class);
        chat = getServer().getServicesManager().getRegistration(Chat.class).getProvider();

        // loading combat-tag datastructures
        combatConfigurations = new CombatConfigurations(getConfigLong("combat_duration"));

        // getting and setting up worlds with optional multiple spawn-points
        worldConfiguration = new WorldConfiguration(this, getConfigArray("worlds.list"),
                getConfigInt("worlds.change_every"), 50);

        // loading events
        new PlayerEvents(this);
        new WorldEvents(this);
        new CombatEvents(this);

        // load placeholders
        placeholderAPI = new PlaceholderAPI(this);
        placeholderAPI.addStats("kills", bPlayer -> String.valueOf(bPlayer.getKills()));
        placeholderAPI.addStats("deaths", bPlayer -> String.valueOf(bPlayer.getDeaths()));
        placeholderAPI.addStats("ks", bPlayer -> String.valueOf(bPlayer.getStreak()));
        placeholderAPI.addStats("kd", bPlayer -> {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            long deaths = bPlayer.getDeaths();
            return String.valueOf(decimalFormat.format(
                    (float) bPlayer.getKills() / (float) (deaths == 0 ? 1 : deaths))
            );
        });
        placeholderAPI.addStats("map", bPlayer -> {
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
        new Spawn(this);

        // loading commands
        new Reply(this);
        new Notifiche(this);
        new Message(this);
        new ForceSave(this);

        // loading tasks
        new AlwaysDay(this, getConfigInt("alwaysday.repeat_every"), getConfigInt("alwaysday.set_time"));
        new ActionbarAnnouncer(this, getConfigArray("actionbar.messages"),
                getConfigInt("actionbar.duration"), getConfigInt("actionbar.repeat_every"));

        //
        // END COMMANDS AND TASKS RELATED
        //
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        // updating mirror for saving data
        playerBank.saveAll();
        // saving data
        musician.play();
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
