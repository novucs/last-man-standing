package net.novucs.lms.settings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A factory for item creation.
 */
public class ItemFactory {

    private final Material material;
    private final byte data;
    private final int max;
    private final int min;
    private final String name;
    private final ImmutableList<String> lore;
    private final ImmutableMap<Enchantment, Integer> enchantments;

    public ItemFactory(Material material, byte data, int max, int min, String name, ImmutableList<String> lore,
                       ImmutableMap<Enchantment, Integer> enchantments) {
        this.material = material;
        this.data = data;
        this.max = max;
        this.min = min;
        this.name = name;
        this.lore = lore;
        this.enchantments = enchantments;
    }

    /**
     * Gets the material.
     *
     * @return the {@link Material}.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Gets the item data.
     *
     * @return the data.
     */
    public byte getData() {
        return data;
    }

    /**
     * Gets the upper limit used in item creation.
     *
     * @return the upper limit or {@code -1} if no upper limit is set.
     */
    public int getMax() {
        return max;
    }

    /**
     * Gets the lower limit used for item creation.
     *
     * @return the lower limit.
     */
    public int getMin() {
        return min;
    }

    /**
     * Gets the item name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the item lore.
     *
     * @return the lore.
     */
    public ImmutableList<String> getLore() {
        return lore;
    }

    /**
     * Gets the item enchantments.
     *
     * @return the enchantments.
     */
    public ImmutableMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    /**
     * Creates a new item with the current factory settings.
     *
     * @return the newly constructed {@link ItemStack}.
     */
    public ItemStack create() {
        // Create the item stack and apply enchantments.
        ItemStack item = new ItemStack(material, getAmount(), data);
        item.addEnchantments(enchantments);


        // Set the display name and lore if valid.
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.setDisplayName(name);
        }

        if (lore != null) {
            meta.setLore(lore);
        }

        // Apply the new metadata to the item stack.
        item.setItemMeta(meta);

        // Return the newly constructed item.
        return item;
    }

    /**
     * Gets a random number between the upper and lower limits. In the event of
     * no upper limit being defined, the lower limit will be returned.
     *
     * @return the random item amount between the bounds.
     */
    private int getAmount() {
        if (isMaxSet()) {
            return min;
        } else {
            return ThreadLocalRandom.current().nextInt(min, max);
        }
    }

    /**
     * Gets if the maximum value has been set.
     *
     * @return {@code true} if it has been set.
     */
    private boolean isMaxSet() {
        return max == -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemFactory that = (ItemFactory) o;
        return data == that.data &&
                max == that.max &&
                min == that.min &&
                material == that.material &&
                Objects.equals(name, that.name) &&
                Objects.equals(lore, that.lore) &&
                Objects.equals(enchantments, that.enchantments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, data, max, min, name, lore, enchantments);
    }

    @Override
    public String toString() {
        return "ItemFactory{" +
                "material=" + material +
                ", data=" + data +
                ", max=" + max +
                ", min=" + min +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", enchantments=" + enchantments +
                '}';
    }
}
