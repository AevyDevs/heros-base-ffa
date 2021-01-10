package net.herospvp.base_ffa.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.herospvp.base_ffa.utils.StringFormat;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Hikari {

    @Getter
    private final DataSource dataSource;
    private final String table;

    public Hikari(String ip, String port, String database,
                  String table, String user, String password) throws Exception {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database + "?useSSL=false&characterEncoding=utf8");
        config.setUsername(user);
        if (password != null) {
            config.setPassword(password);
        }
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.setMaximumPoolSize(2);

        dataSource = new HikariDataSource(config);
        this.table = table;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table +
                    " (username CHAR(16) NOT NULL, kills INTEGER UNSIGNED, deaths INTEGER UNSIGNED," +
                    " streak INTEGER UNSIGNED, noDeaths INTEGER UNSIGNED, noPings INTEGER UNSIGNED," +
                    " noMsg INTEGER UNSIGNED, PRIMARY KEY(username));");
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT * FROM " + table);
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
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    public void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        if (connection != null) try { connection.close(); } catch (Exception ignored) {}
        if (preparedStatement != null) try { preparedStatement.close(); } catch (Exception ignored) {}
        if (resultSet != null) try { resultSet.close(); } catch (Exception ignored) {}
    }

    public void saveAll() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();

            for (Map.Entry<String, Integer[]> entry : RAM.getStoredPlayers().entrySet()) {
                String playerName = entry.getKey();
                Integer[] integers = entry.getValue();

                preparedStatement = connection.prepareStatement("SELECT * FROM " + table + " WHERE username = "
                        + StringFormat.forMysql(playerName));
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    preparedStatement = connection.prepareStatement("INSERT INTO " + table
                            + " (username, kills, deaths, streak, noDeaths, noPings, noMsg) VALUES ("
                            + StringFormat.forMysql(playerName) + ", " + integers[0] +
                            ", " + integers[1] + ", " + integers[2] + ", " + integers[3] +
                            ", " + integers[4] + ", " + integers[5] + ");");
                } else {
                    preparedStatement = connection.prepareStatement("UPDATE " + table + " SET kills = ?, deaths = ?, " +
                            "streak = ?, noDeaths = ?, noPings = ?, noMsg = ? WHERE username = "
                            + StringFormat.forMysql(playerName));

                    int j = 0;
                    for (int i = 1; i < 7; i++) {
                        preparedStatement.setInt(i, integers[j]);
                        j++;
                    }
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

}