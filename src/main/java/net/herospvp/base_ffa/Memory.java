package net.herospvp.base_ffa;

import lombok.Getter;
import lombok.Setter;
import net.herospvp.base_ffa.configuration.CombatTagConfiguration;
import net.herospvp.base_ffa.configuration.kit.Kit;
import net.herospvp.base_ffa.database.Hikari;
import net.herospvp.base_ffa.database.RAM;
import net.herospvp.database.Musician;
import net.herospvp.database.items.Notes;
import net.herospvp.database.items.Papers;
import net.milkbowl.vault.chat.Chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class Memory {

    @Getter @Setter @Deprecated
    private static Hikari hikari;
    @Getter @Setter
    private static Kit kit;
    @Getter @Setter
    private static CombatTagConfiguration combatTagConfiguration;
    @Getter @Setter
    private static Chat chat;

    @Getter @Setter
    private static Musician musician;
    @Getter @Setter
    private static String table;

    public static Papers init() {
        return (connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                String[] strings = {"username CHAR(16) NOT NULL", "kills INTEGER UNSIGNED", "deaths INTEGER UNSIGNED",
                        "streak INTEGER UNSIGNED", "noDeaths INTEGER UNSIGNED", "noPings INTEGER UNSIGNED",
                        "noMsg INTEGER UNSIGNED", "PRIMARY KEY(username)"};

                preparedStatement = connection.prepareStatement(new Notes(table).createTable(strings));
                preparedStatement.executeUpdate();

                preparedStatement = connection.prepareStatement(new Notes(table).selectAll());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String playerName = resultSet.getString(1);
                    Integer[] integers = new Integer[6];

                    int j = 0;
                    for (int i = 2; i < 8; i++) {
                        integers[j] = resultSet.getInt(i);
                        j++;
                    }
                    RAM.setEveryData(playerName, integers);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(null, preparedStatement, resultSet);
            }
        };
    }

    public static Papers saveAll() {
        return (connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                for (Map.Entry<String, Integer[]> entry : RAM.getStoredPlayers().entrySet()) {
                    String playerName = entry.getKey();
                    Integer[] integers = entry.getValue();

                    // database-lib
                    preparedStatement = connection.prepareStatement(new Notes(table).selectWhere("username", playerName));
                    resultSet = preparedStatement.executeQuery();
                    if (!resultSet.next()) {
                        // database-lib
                        String[] strings = {"username", "kills", "deaths", "streak", "noDeaths", "noPings", "noMsg"};
                        preparedStatement = connection.prepareStatement(new Notes(table).insert(strings, integers));
                    } else {
                        // database-lib
                        String[] fields = {"kills", "deaths", "streak", "noDeaths", "noPings", "noMsg"};
                        preparedStatement = connection.prepareStatement(new Notes(table)
                                .update(fields, integers, "username", playerName));
                    }
                    preparedStatement.executeUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(null, preparedStatement, resultSet);
            }
        };
    }

}
