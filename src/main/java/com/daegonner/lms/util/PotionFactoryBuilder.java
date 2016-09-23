package com.daegonner.lms.util;

import com.google.common.collect.ImmutableList;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The builder for a {@link PotionFactory}.
 */
public class PotionFactoryBuilder {

    private double chance;
    private int max = -1;
    private int min = 1;
    private String name;
    private List<String> lore = new ArrayList<>();
    private PotionType potionType;
    private boolean upgraded;
    private boolean extended;
    private boolean splash;

    /**
     * Sets the chance for the item to be spawned.
     *
     * @param chance the chance.
     * @return this.
     */
    public PotionFactoryBuilder chance(double chance) {
        this.chance = chance;
        return this;
    }

    /**
     * Sets the maximum amount of the potions when called.
     *
     * @param max the upper potion limit on creation.
     * @return this.
     */
    public PotionFactoryBuilder max(int max) {
        this.max = max;
        return this;
    }

    /**
     * Sets the minimum amount of potions when called.
     *
     * @param min the lower potion limit on creation.
     * @return this.
     */
    public PotionFactoryBuilder min(int min) {
        this.min = min;
        return this;
    }

    /**
     * Sets the name of the potion created.
     *
     * @param name the potion name.
     * @return this.
     */
    public PotionFactoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the lore of the potion created.
     *
     * @param lore the potion lore.
     * @return this.
     */
    public PotionFactoryBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Adds to the lore of the potion created.
     *
     * @param lore a potion lore line.
     * @return this.
     */
    public PotionFactoryBuilder addLore(String lore) {
        this.lore.add(lore);
        return this;
    }

    /**
     * Sets the potion type created.
     *
     * @param potionType the {@link PotionType}.
     * @return this.
     */
    public PotionFactoryBuilder potionType(PotionType potionType) {
        this.potionType = potionType;
        return this;
    }

    /**
     * Sets whether the potion created should be more potent.
     *
     * @param upgraded {@code} true if more potent.
     * @return this.
     */
    public PotionFactoryBuilder upgraded(boolean upgraded) {
        this.upgraded = upgraded;
        return this;
    }

    /**
     * Sets whether the potion created should be extended.
     *
     * @param extended {@code} true if extended.
     * @return this.
     */
    public PotionFactoryBuilder extended(boolean extended) {
        this.extended = extended;
        return this;
    }

    /**
     * Sets whether the potion created should be splash.
     *
     * @param splash {@code} true if splash.
     * @return this.
     */
    public PotionFactoryBuilder splash(boolean splash) {
        this.splash = splash;
        return this;
    }

    /**
     * Builds the {@link PotionFactory} with the current settings.
     *
     * @return the potion factory.
     */
    public PotionFactory build() {
        return new PotionFactory(chance, max, min, name, ImmutableList.copyOf(lore), potionType, upgraded, extended,
                splash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotionFactoryBuilder that = (PotionFactoryBuilder) o;
        return Double.compare(that.chance, chance) == 0 &&
                max == that.max &&
                min == that.min &&
                upgraded == that.upgraded &&
                extended == that.extended &&
                splash == that.splash &&
                Objects.equals(name, that.name) &&
                Objects.equals(lore, that.lore) &&
                potionType == that.potionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chance, max, min, name, lore, potionType, upgraded, extended, splash);
    }

    @Override
    public String toString() {
        return "PotionFactoryBuilder{" +
                "chance=" + chance +
                ", max=" + max +
                ", min=" + min +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", potionType=" + potionType +
                ", upgraded=" + upgraded +
                ", extended=" + extended +
                ", splash=" + splash +
                '}';
    }
}
