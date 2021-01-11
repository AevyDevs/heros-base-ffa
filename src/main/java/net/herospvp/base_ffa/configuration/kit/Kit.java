package net.herospvp.base_ffa.configuration.kit;

import lombok.Getter;
import net.herospvp.base_ffa.Main;
import net.herospvp.base_ffa.Memory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Kit {

    @Getter
    private final ItemStack[] armor;
    @Getter
    private final ItemStack[] hotBar;
    @Getter
    private final ItemStack[] killRewards;
    @Getter
    private final PotionEffect[] potionEffects;

    public Kit(ItemStack[] armor, ItemStack[] hotBar, ItemStack[] killRewards, PotionEffect[] potionEffects) {
        this.armor = armor;
        this.hotBar = hotBar;
        this.killRewards = killRewards;
        this.potionEffects = potionEffects;
    }

    public ItemStack[] enchantAllArmor(Enchants enchants) {
        for (ItemStack itemStack : armor) {
            itemStack.addEnchantment(enchants.getEnchantment(), enchants.getLevel());
        }
        return armor;
    }

    public ItemStack[] enchantArmor(int i, Enchants enchants) {
        armor[i].addEnchantment(enchants.getEnchantment(), enchants.getLevel());
        return armor;
    }

    public ItemStack[] enchantAllHotBar(Enchants enchants) {
        for (ItemStack itemStack : hotBar) {
            itemStack.addEnchantment(enchants.getEnchantment(), enchants.getLevel());
        }
        return hotBar;
    }

    public ItemStack enchantHotBar(int i, Enchants enchants) {
        hotBar[i].addEnchantment(enchants.getEnchantment(), enchants.getLevel());
        return hotBar[i];
    }

    public void setPlayerHotBar(Player player) {
        for (int i = 0; i < hotBar.length; i++) {
            player.getInventory().setItem(i, hotBar[i]);
        }
    }

    public void setPlayerArmor(Player player) {
        player.getInventory().setArmorContents(armor);
    }

    public void assignPotionEffects(Player player) {

        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }

        if (potionEffects == null) return;

        Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> {
            PotionEffect[] potionEffects = getPotionEffects();

            for (PotionEffect potionEffect : potionEffects) {
                player.addPotionEffect(potionEffect);
            }
        }, 10L);
    }

    public void assignKillRewards(Player player) {
        player.getInventory().setArmorContents(null);
        setPlayerArmor(player);

        for (ItemStack itemStack : killRewards) {
            player.getInventory().addItem(itemStack);
        }

        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack == null) continue;

            for (ItemStack stack : Memory.getKit().getArmor()) {

                if (stack.getType().equals(itemStack.getType())) {
                    player.getInventory().remove(itemStack);
                }
            }

        }
    }

}
