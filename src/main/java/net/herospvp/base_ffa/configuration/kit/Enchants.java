package net.herospvp.base_ffa.configuration.kit;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;

public class Enchants {

    @Getter
    private final Enchantment enchantment;
    @Getter
    private final int level;

    public Enchants(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

}
