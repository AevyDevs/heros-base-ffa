package net.herospvp.base.storage;

import lombok.Getter;
import net.herospvp.base.Base;
import net.herospvp.base.utils.StringFormat;
import net.herospvp.database.items.Notes;
import net.herospvp.database.items.Papers;
import org.bukkit.Bukkit;
import org.bukkit.Note;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
    private final TreeSet<String> playersToModify;
    @Getter
    private final Map<Player, Player> lastMessages;

    public Bank(Base instance) {
        this.instance = instance;
        this.table = instance.getTable();
        this.fieldsOfTable = instance.getFieldsOfTable();
        this.onlinePlayers = new TreeSet<>();
        this.storedPlayers = new HashMap<>();
        this.lastMessages = new HashMap<>();
        this.playersToModify = new TreeSet<>();
        this.stringFormat = instance.getStringFormat();
        this.notes = new Notes(table);
    }

    public void newEntry(Player player) {
        String playerName = player.getName();
        if (!storedPlayers.containsKey(playerName)) {
            storedPlayers.put(playerName, new Object[] { 0, 0, 0, true, true, true });
        }
        playersToModify.add(playerName);
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

    public boolean toModify(String playerName) { return playersToModify.contains(playerName); }


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
            if (!instance.isLoaded()) {
                instance.setLoaded(true);
                System.out.println("[BaseFFA] Players may now join the server!");
            }
        });
    }

    public Papers save(boolean shutdown) {
        return (connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            try {
                String[] newFields = new String[6];
                System.arraycopy(fieldsOfTable, 1, newFields, 0, fieldsOfTable.length - 1);

                List<PreparedStatement> preparedStatementList = new ArrayList<>();

                for (Map.Entry<String, Object[]> entry : storedPlayers.entrySet()) {
                    String playerName = entry.getKey();
                    Object[] objects = entry.getValue();

                    if (!toModify(playerName)) continue;

                    Object[] newObjects = new Object[7];
                    newObjects[0] = playerName;
                    System.arraycopy(objects, 0, newObjects, 1, objects.length);

                    preparedStatement = connection.prepareStatement(
                            notes.insertIfNotExist(fieldsOfTable, newObjects, "username", playerName)
                    );
                    preparedStatement.addBatch();

                    System.out.println(notes.insertIfNotExist(fieldsOfTable, newObjects, "username", playerName));

                    preparedStatementList.add(connection.prepareStatement(
                            notes.update(newFields, objects, "username", playerName)
                    ));
                }

                if (preparedStatement != null) {
                    preparedStatement.executeBatch();
                    for (PreparedStatement statement : preparedStatementList) {
                        statement.executeUpdate();
                    }
                }

                synchronized (instance) {
                    playersToModify.clear();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        playersToModify.add(player.getName());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(null, preparedStatement, null);
                if (shutdown)
                    instance.getLatch().countDown();
            }
        };
    }

}
