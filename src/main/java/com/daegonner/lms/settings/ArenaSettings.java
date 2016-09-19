package com.daegonner.lms.settings;

import com.daegonner.lms.reward.Reward;
import com.daegonner.lms.util.GenericUtils;
import com.daegonner.lms.util.ItemFactory;
import com.daegonner.lms.util.ItemFactoryBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArenaSettings {

    private final FileConfiguration config;
    private final String section;

    private boolean disableHunger;
    private double killMoneyMax;
    private double killMoneyMin;
    private List<Reward> rewards;
    private List<ItemStack> inventory;
    private ItemStack headArmour;
    private ItemStack bodyArmour;
    private ItemStack legArmour;
    private ItemStack bootArmour;

    public ArenaSettings(FileConfiguration config, String section) {
        this.config = config;
        this.section = section;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getSection() {
        return section;
    }

    public boolean isDisableHunger() {
        return disableHunger;
    }

    public double getKillMoneyMax() {
        return killMoneyMax;
    }

    public double getKillMoneyMin() {
        return killMoneyMin;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public List<ItemStack> getInventory() {
        return inventory;
    }

    public ItemStack getHeadArmour() {
        return headArmour;
    }

    public ItemStack getBodyArmour() {
        return bodyArmour;
    }

    public ItemStack getLegArmour() {
        return legArmour;
    }

    public ItemStack getBootArmour() {
        return bootArmour;
    }

    private boolean getBoolean(String path, boolean def) {
        config.addDefault(section + path, def);
        return config.getBoolean(path);
    }

    private int getInt(String path, int def) {
        config.addDefault(section + path, def);
        return config.getInt(path);
    }

    private long getLong(String path, long def) {
        config.addDefault(section + path, def);
        return config.getLong(path);
    }

    private double getDouble(String path, double def) {
        config.addDefault(section + path, def);
        return config.getDouble(path);
    }

    private String getString(String path, String def) {
        config.addDefault(section + path, def);
        return config.getString(path);
    }

    private ConfigurationSection getOrCreateSection(String key) {
        key = section + key;
        return config.getConfigurationSection(key) == null ?
                config.createSection(key) : config.getConfigurationSection(key);
    }

    private ConfigurationSection getOrDefaultSection(String key) {
        key = section + key;
        return config.getConfigurationSection(key).getKeys(false).isEmpty() ?
                config.getDefaults().getConfigurationSection(key) : config.getConfigurationSection(key);
    }

    private <T> List<?> getList(String key, List<T> def) {
        key = section + key;
        config.addDefault(key, def);
        return config.getList(key, config.getList(key));
    }

    private <E> List<E> getList(String key, List<E> def, Class<E> type) {
        key = section + key;
        try {
            return castList(type, getList(key, def));
        } catch (ClassCastException e) {
            return def;
        }
    }

    private <E> List<E> castList(Class<? extends E> type, List<?> toCast) throws ClassCastException {
        return toCast.stream().map(type::cast).collect(Collectors.toList());
    }

    public void load() {
        disableHunger = getBoolean("disable-hunger", true);
        killMoneyMax = getDouble("kill-money.max", -1);
        killMoneyMin = getDouble("kill-money.min", -1);
    }

    private ItemStack parseItem(Map<?, ?> data) {
        ItemFactoryBuilder builder = new ItemFactoryBuilder();
        GenericUtils.getEnum(Material.class, data, "material").ifPresent(builder::material);
        GenericUtils.getInt(data, "data").ifPresent(b -> builder.data(b.byteValue()));
        GenericUtils.getInt(data, "amount").ifPresent(builder::min);
        GenericUtils.getString(data, "name").ifPresent(builder::name);
        GenericUtils.getList(data, "lore").ifPresent(l -> builder.lore(castList(String.class, l)));
        GenericUtils.getMap(data, "enchantments").ifPresent(m -> builder.enchantments(parseEnchantments(m)));
        return builder.build().create();
    }

    private ItemFactory parseItemFactory(Map<?, ?> data) {
        ItemFactoryBuilder builder = new ItemFactoryBuilder();
        GenericUtils.getEnum(Material.class, data, "material").ifPresent(builder::material);
        GenericUtils.getInt(data, "data").ifPresent(b -> builder.data(b.byteValue()));
        GenericUtils.getInt(data, "amount.max").ifPresent(builder::max);
        GenericUtils.getInt(data, "amount.min").ifPresent(builder::min);
        GenericUtils.getString(data, "name").ifPresent(builder::name);
        GenericUtils.getList(data, "lore").ifPresent(l -> builder.lore(castList(String.class, l)));
        GenericUtils.getMap(data, "enchantments").ifPresent(m -> builder.enchantments(parseEnchantments(m)));
        return builder.build();
    }

    private Map<Enchantment, Integer> parseEnchantments(Map<?, ?> toParse) {
        Map<Enchantment, Integer> target = new HashMap<>();
        for (Map.Entry<?, ?> entry : toParse.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            Enchantment enchantment = null;
            int level = 0;

            if (key instanceof String) {
                enchantment = Enchantment.getByName((String) key);
            }

            if (value instanceof Integer) {
                level = (Integer) value;
            }

            if (level > 0 && enchantment != null) {
                target.put(enchantment, level);
            }
        }
        return target;
    }
}
