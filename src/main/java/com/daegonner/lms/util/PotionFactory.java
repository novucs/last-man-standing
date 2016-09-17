package com.daegonner.lms.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Objects;

/**
 * A factory for potion creation.
 */
public class PotionFactory extends ItemFactory {

    private final PotionType potionType;
    private final boolean upgraded;
    private final boolean extended;
    private final boolean splash;

    public PotionFactory(int max, int min, String name, ImmutableList<String> lore, PotionType potionType,
                         boolean upgraded, boolean extended, boolean splash) {
        super(Material.POTION, (byte) 0, max, min, name, lore, ImmutableMap.of());
        this.potionType = potionType;
        this.upgraded = upgraded;
        this.extended = extended;
        this.splash = splash;
    }

    /**
     * Gets the potion type.
     *
     * @return the {@link PotionType}.
     */
    public PotionType getPotionType() {
        return potionType;
    }

    /**
     * Gets if the blueprint is upgraded.
     *
     * @return {@code true} if upgraded.
     */
    public boolean isUpgraded() {
        return upgraded;
    }

    /**
     * Gets if the blueprint is extended.
     *
     * @return {@code true} if extended.
     */
    public boolean isExtended() {
        return extended;
    }

    /**
     * Gets if the blueprint is splash.
     *
     * @return {@code true} if splash.
     */
    public boolean isSplash() {
        return splash;
    }

    @Override
    public ItemStack create() {
        ItemStack item = super.create();
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        PotionData data = new PotionData(potionType, upgraded, extended);
        meta.setBasePotionData(data);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PotionFactory that = (PotionFactory) o;
        return upgraded == that.upgraded &&
                extended == that.extended &&
                splash == that.splash &&
                potionType == that.potionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), potionType, upgraded, extended, splash);
    }

    @Override
    public String toString() {
        return "PotionFactory{" +
                "potionType=" + potionType +
                ", upgraded=" + upgraded +
                ", extended=" + extended +
                ", splash=" + splash +
                '}';
    }
}
