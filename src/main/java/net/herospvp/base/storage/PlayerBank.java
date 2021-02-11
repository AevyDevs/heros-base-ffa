package net.herospvp.base.storage;

import lombok.Getter;
import net.herospvp.base.Base;
import net.herospvp.database.Musician;
import net.herospvp.database.items.Notes;
import net.herospvp.database.items.Papers;
import net.herospvp.heroscore.HerosCore;
import net.herospvp.heroscore.objects.HPlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerBank {

    private final Base instance;
    private final HerosCore herosCore;

    private final Notes notes;
    private final Musician musician;
    private final String[] fieldsOfTable, cleanFieldsOfTable;

    @Getter
    private final Map<BPlayer, BPlayer> lastMessages;

    @Getter
    private final List<BPlayer> bPlayers;

    public PlayerBank(Base instance) {
        this.instance = instance;
        this.herosCore = instance.getHerosCore();

        this.notes = new Notes(instance.getTable());
        this.musician = instance.getMusician();

        this.fieldsOfTable = instance.getFieldsOfTable();
        String[] strings = new String[fieldsOfTable.length - 1];
        System.arraycopy(fieldsOfTable, 1, strings, 0, fieldsOfTable.length - 1);
        this.cleanFieldsOfTable = strings;

        this.lastMessages = new HashMap<>();

        this.bPlayers = new ArrayList<>();
    }

    public Papers startup() {
        return ((connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(
                        notes.createTable(instance.getTableCreateFields())
                );
                preparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement);
            }
        });
    }

    public Papers load(Player player, Runnable runnable) {
        return (connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(
                        notes.selectAllWhere("username", player.getName())
                );
                resultSet = preparedStatement.executeQuery();

                HPlayer hPlayer = herosCore.getPlayersHandler().getPlayer(player.getUniqueId());
                BPlayer bPlayer;

                if (!resultSet.next()) {
                    bPlayer = new BPlayer(hPlayer, player, 0, 0, 0,
                            false, false, false, false);

                    preparedStatement = connection.prepareStatement(
                            notes.insert(fieldsOfTable, new Object[]{
                                    player.getName(), bPlayer.getKills(), bPlayer.getDeaths(), bPlayer.getStreak(),
                                    bPlayer.isNoDeaths(), bPlayer.isNoPings(), bPlayer.isNoMsg()
                            })
                    );
                    preparedStatement.execute();
                } else {
                    bPlayer = new BPlayer(hPlayer, player, resultSet.getInt(2), resultSet.getInt(3),
                            resultSet.getInt(4), resultSet.getBoolean(5), resultSet.getBoolean(6),
                            resultSet.getBoolean(7), false);
                }

                synchronized (bPlayers) {
                    bPlayers.add(bPlayer);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement, resultSet);
                runnable.run();
            }
        };
    }

    public Papers save(BPlayer bPlayer) {
        return (connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            try {
                if (!bPlayer.isEdited()) return;

                preparedStatement = connection.prepareStatement(
                        notes.update(cleanFieldsOfTable,
                                new Object[] { bPlayer.getKills(), bPlayer.getDeaths(),
                                        bPlayer.getStreak(), bPlayer.isNoDeaths(), bPlayer.isNoPings(),
                                        bPlayer.isNoMsg() },
                                "username", bPlayer.getPlayer().getName()
                        )
                );
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement);

                synchronized (bPlayers) {
                    bPlayers.remove(bPlayer);
                }

                synchronized (lastMessages) {
                    lastMessages.remove(bPlayer);
                }

            }
        };
    }

    public void saveAll() {
        for (BPlayer bPlayer : bPlayers) {
            musician.update(save(bPlayer));
        }
    }

    public BPlayer getBPlayerFrom(Player player) {
        for (BPlayer bPlayer : bPlayers) {
            if (bPlayer.getPlayer().equals(player)) {
                return bPlayer;
            }
        }
        return null;
    }

    public BPlayer getBPlayerFrom(String playerName) {
        for (BPlayer bPlayer : bPlayers) {
            if (bPlayer.getPlayer().getName().equalsIgnoreCase(playerName)) {
                return bPlayer;
            }
        }
        return null;
    }

}
