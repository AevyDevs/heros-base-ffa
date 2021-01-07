package net.herospvp.base_ffa.database.extensions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.herospvp.base_ffa.database.RAM;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPI extends PlaceholderExpansion {

    public PAPI() {
        register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "sr";
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
    public String onPlaceholderRequest(Player player, String identifier) {

        String response;

        switch (identifier.toLowerCase()) {
            case "deaths": {
                response = String.valueOf(RAM.getDeaths(player));
                break;
            }
            case "kills": {
                response = String.valueOf(RAM.getKills(player));
                break;
            }
            case "ks": {
                response = String.valueOf(RAM.getStreak(player));
                break;
            }
            case "kda": {
                int deaths = RAM.getDeaths(player);
                response = String.valueOf((float) (RAM.getKills(player) / (deaths == 0 ? 1 : deaths)));
                break;
            }
            default: {
                response = "?";
                break;
            }
        }

        return response;
    }

}
