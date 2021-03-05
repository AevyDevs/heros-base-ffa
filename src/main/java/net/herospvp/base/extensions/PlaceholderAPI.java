package net.herospvp.base.extensions;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.herospvp.base.Base;
import net.herospvp.base.storage.BPlayer;
import net.herospvp.base.storage.PlayerBank;
import net.herospvp.base.utils.lambdas.PlaceholderAPILambda;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderAPI extends PlaceholderExpansion {

    @Getter
    private final Map<String, PlaceholderAPILambda> retrieveStats;
    private final PlayerBank playerBank;

    public PlaceholderAPI(Base instance) {
        this.retrieveStats = new HashMap<>();
        this.playerBank = instance.getPlayerBank();
        register();
    }

    public void addStats(String string, PlaceholderAPILambda lambda) {
        retrieveStats.put(string, lambda);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "base";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Sorridi";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        BPlayer bPlayer = playerBank.getBPlayerFrom(player);
        if (bPlayer == null) {
            return "Caricando...";
        }

        return retrieveStats.containsKey(params) ? retrieveStats.get(params).func(bPlayer) : "?";
    }

}
