package net.herospvp.base.storage;

import lombok.Getter;
import net.herospvp.base.Base;
import net.herospvp.base.utils.StringFormat;
import net.herospvp.database.items.Notes;
import net.herospvp.database.items.Papers;
import org.bukkit.Note;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Bank {

    private final Base instance;
    private final StringFormat stringFormat;
    private final Notes notes;

    @Getter
    private final String table;
    @Getter
    private final String[] fieldsOfTable;

    @Getter
    private final TreeSet<String> onlinePlayers;
    @Getter // 0 = kills, 1 = deaths, 2 = streak, 3 = noDeaths, 4 = noPings, 5 = noMsg
    private final Map<String, Object[]> storedPlayers;
    @Getter
    private final Map<Player, Player> lastMessages;

    public Bank(Base instance) {
        this.instance = instance;
        this.table = instance.getTable();
        this.fieldsOfTable = instance.getFieldsOfTable();
        this.onlinePlayers = new TreeSet<>();
        this.storedPlayers = new HashMap<>();
        this.lastMessages = new HashMap<>();
        this.stringFormat = instance.getStringFormat();
        this.notes = new Notes(table);
    }

    public void newEntry(Player player) {
        if (!getStoredPlayers().containsKey(player.getName())) {
            storedPlayers.put(player.getName(), new Object[] {0, 0, 0, true, true, true});
        }
    }

    public void addKills(Object object, int howMany) {
        String string = stringFormat.convert(object);
        storedPlayers.get(string)[0] = getKills(string) + howMany;
        addStreak(string, howMany);
    }

    public void addDeaths(Object object, int howMany) {
        String string = stringFormat.convert(object);
        storedPlayers.get(string)[1] = getDeaths(string) + howMany;
        storedPlayers.get(string)[2] = 0;
    }

    public void addStreak(Object object, int howMany) {
        String string = stringFormat.convert(object);
        storedPlayers.get(string)[2] = getStreak(string) + howMany;
    }

    public int getKills(Object object) {
        return (int) storedPlayers.get(stringFormat.convert(object))[0];
    }

    public int getDeaths(Object object) {
        return (int) storedPlayers.get(stringFormat.convert(object))[1];
    }

    public int getStreak(Object object) {
        return (int) storedPlayers.get(stringFormat.convert(object))[2];
    }

    public boolean wantsDeaths(Object object) {
        return (boolean) storedPlayers.get(stringFormat.convert(object))[3];
    }

    public boolean wantsPings(Object object) {
        return (boolean) storedPlayers.get(stringFormat.convert(object))[4];
    }

    public boolean wantsMsg(Object object) {
        return (boolean) storedPlayers.get(stringFormat.convert(object))[5];
    }


    public void changeDeathsIdea(Player player) { storedPlayers.get(player.getName())[3] = !wantsDeaths(player); }

    public void changePingsIdea(Player player) { storedPlayers.get(player.getName())[4] = !wantsPings(player); }

    public void changeMsgIdea(Player player) { storedPlayers.get(player.getName())[5] = !wantsMsg(player); }


    public boolean isOnline(String playerName) { return onlinePlayers.contains(playerName); }


    public Papers init() {
        return ((connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {

                preparedStatement = connection.prepareStatement(
                        notes.createTable(instance.getTableCreateFields())
                );
                preparedStatement.execute();

                preparedStatement = connection.prepareStatement(
                        notes.selectAll()
                );
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String playerName = resultSet.getString(1);
                    Object[] objects = new Object[6];

                    int j = 0;
                    for (int i = 2; i < 8; i++) {
                        objects[j] = resultSet.getObject(i);
                        j++;
                    }

                    storedPlayers.put(playerName, objects);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(null, preparedStatement, resultSet);
            }
            instance.setLoaded(true);
            System.out.println("[BaseFFA] Players may now join the server!");
        });
    }

    public Papers save() {
        return (connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                for (Map.Entry<String, Object[]> entry : storedPlayers.entrySet()) {
                    String playerName = entry.getKey();
                    Object[] objects = entry.getValue();

                    Object[] newObjects = new Object[7];
                    newObjects[1] = playerName;

                    System.arraycopy(objects, 0, newObjects, 1, objects.length);

                    preparedStatement = connection.prepareStatement(
                            notes.insertIfNotExist(fieldsOfTable, newObjects, "username", playerName)
                    );

                    preparedStatement.addBatch();
                }
                if (preparedStatement != null)
                    preparedStatement.executeBatch();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(null, preparedStatement, resultSet);
            }
        };
    }

}
