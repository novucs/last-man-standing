package com.daegonner.lms.util;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

/**
 * The builder for an {@link ItemFactory}.
 */
public class ItemFactoryBuilder {

    private double chance = 1;
    private Material material = Material.AIR;
    private byte data = 0;
    private int max = -1;
    private int min = 1;
    private String name;
    private List<String> lore = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();

    /**
     * Sets the chance for the item to be spawned.
     *
     * @param chance the chance.
     * @return this.
     */
    public ItemFactoryBuilder chance(double chance) {
        this.chance = chance;
        return this;
    }

    /**
     * Sets the material for item creation.
     *
     * @param material the {@link Material}.
     * @return this.
     */
    public ItemFactoryBuilder material(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Sets the data for item creation.
     *
     * @param data the item data.
     * @return this.
     */
    public ItemFactoryBuilder data(byte data) {
        this.data = data;
        return this;
    }

    /**
     * Sets the maximum amount of the items when called.
     *
     * @param max the upper item limit on creation.
     * @return this.
     */
    public ItemFactoryBuilder max(int max) {
        this.max = max;
        return this;
    }

    /**
     * Sets the minimum amount of the items when called.
     *
     * @param min the lower item limit on creation.
     * @return this.
     */
    public ItemFactoryBuilder min(int min) {
        this.min = min;
        return this;
    }

    /**
     * Sets the name of the item created.
     *
     * @param name the item name.
     * @return this.
     */
    public ItemFactoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the lore of the item created.
     *
     * @param lore the item lore.
     * @return this.
     */
    public ItemFactoryBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Adds to the lore of the item created.
     *
     * @param lore a item lore line.
     * @return this.
     */
    public ItemFactoryBuilder addLore(String lore) {
        this.lore.add(lore);
        return this;
    }

    /**
     * Sets the enchantments of the item created.
     *
     * @param enchantments the item enchantments.
     * @return this.
     */
    public ItemFactoryBuilder enchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    /**
     * Applies an enchantment to the item created.
     *
     * @param enchantment the {@link Enchantment} to use.
     * @param level       the level of the enchantment.
     * @return this.
     */
    public ItemFactoryBuilder enchant(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Builds the {@link ItemFactory} with the current settings.
     *
     * @return the item factory.
     */
    public ItemFactory build() {
        return new ItemFactory(chance, material, data, max, min, name, ImmutableList.copyOf(lore),
                ImmutableMap.copyOf(enchantments));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemFactoryBuilder builder = (ItemFactoryBuilder) o;
        return Double.compare(builder.chance, chance) == 0 &&
                data == builder.data &&
                max == builder.max &&
                min == builder.min &&
                material == builder.material &&
                Objects.equals(name, builder.name) &&
                Objects.equals(lore, builder.lore) &&
                Objects.equals(enchantments, builder.enchantments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chance, material, data, max, min, name, lore, enchantments);
    }

    @Override
    public String toString() {
        return "ItemFactoryBuilder{" +
                "chance=" + chance +
                ", material=" + material +
                ", data=" + data +
                ", max=" + max +
                ", min=" + min +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", enchantments=" + enchantments +
                '}';
    }
}
