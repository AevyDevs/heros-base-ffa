package net.herospvp.base_ffa;

import lombok.Getter;
import lombok.Setter;
import net.herospvp.base_ffa.configuration.CombatTagConfiguration;
import net.herospvp.base_ffa.configuration.kit.Kit;
import net.herospvp.base_ffa.database.Hikari;
import net.milkbowl.vault.chat.Chat;

public class Memory {

    @Getter @Setter
    private static Hikari hikari;
    @Getter @Setter
    private static Kit kit;
    @Getter @Setter
    private static CombatTagConfiguration combatTagConfiguration;
    @Getter @Setter
    private static Chat chat;

}
