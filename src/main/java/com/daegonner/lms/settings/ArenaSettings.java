package com.daegonner.lms.settings;

import com.daegonner.lms.reward.CommandReward;
import com.daegonner.lms.reward.ItemReward;
import com.daegonner.lms.reward.Reward;
import com.daegonner.lms.reward.RewardType;
import com.daegonner.lms.util.GenericUtils;
import com.daegonner.lms.util.ItemFactory;
import com.daegonner.lms.util.ItemFactoryBuilder;
import com.daegonner.lms.util.PotionFactoryBuilder;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaSettings {

    private static final ImmutableList<Reward> DEFAULT_REWARDS = ImmutableList.of(
            new ItemReward(new ItemFactoryBuilder()
                    .material(Material.DIAMOND_BLOCK)
                    .max(512)
                    .min(64)
                    .build()),
            new ItemReward(new ItemFactoryBuilder()
                    .material(Material.DIAMOND_SWORD)
                    .chance(0.25)
                    .enchant(Enchantment.DAMAGE_ALL, 5)
                    .enchant(Enchantment.FIRE_ASPECT, 2)
                    .name("&dLMS Sword")
                    .addLore("&eReceived by winning LMS")
                    .build()),
            new CommandReward("tell {player} Congratulations!", CommandReward.SenderType.CONSOLE)
    );

    private static final ImmutableList<ItemStack> DEFAULT_INVENTORY = ImmutableList.of(
            new ItemFactoryBuilder()
                    .material(Material.DIAMOND_SWORD)
                    .enchant(Enchantment.DAMAGE_ALL, 2)
                    .build()
                    .forceCreate(),
            new PotionFactoryBuilder()
                    .potionType(PotionType.INSTANT_HEAL)
                    .splash(true)
                    .upgraded(true)
                    .min(24)
                    .build()
                    .forceCreate()
    );

    private static final ItemStack DEFAULT_HEAD_ARMOUR = new ItemFactoryBuilder()
            .material(Material.DIAMOND_HELMET)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
            .enchant(Enchantment.DURABILITY, 3)
            .build()
            .forceCreate();

    private static final ItemStack DEFAULT_BODY_ARMOUR = new ItemFactoryBuilder()
            .material(Material.DIAMOND_CHESTPLATE)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
            .enchant(Enchantment.DURABILITY, 3)
            .build()
            .forceCreate();

    private static final ItemStack DEFAULT_LEG_ARMOUR = new ItemFactoryBuilder()
            .material(Material.DIAMOND_LEGGINGS)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
            .enchant(Enchantment.DURABILITY, 3)
            .build()
            .forceCreate();

    private static final ItemStack DEFAULT_BOOT_ARMOUR = new ItemFactoryBuilder()
            .material(Material.DIAMOND_BOOTS)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
            .enchant(Enchantment.DURABILITY, 3)
            .build()
            .forceCreate();

    private final FileConfiguration config;
    private final String arenaName;

    private boolean disableHunger;
    private int minPlayers;
    private double killMoneyMax;
    private double killMoneyMin;
    private List<Reward> rewards;
    private List<ItemStack> inventory;
    private ItemStack headArmour;
    private ItemStack bodyArmour;
    private ItemStack legArmour;
    private ItemStack bootArmour;

    public ArenaSettings(FileConfiguration config, String arenaName) {
        this.config = config;
        this.arenaName = arenaName;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getArenaName() {
        return arenaName;
    }

    public int getMinPlayers() {
        return minPlayers;
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
        String defPath = "arena-settings.default." + path;
        String curPath = "arena-settings." + arenaName + "." + path;
        config.addDefault(defPath, def);
        return config.getBoolean(curPath, config.getBoolean(defPath));
    }

    private int getInt(String path, int def) {
        String defPath = "arena-settings.default." + path;
        String curPath = "arena-settings." + arenaName + "." + path;
        config.addDefault(defPath, def);
        return config.getInt(curPath, config.getInt(defPath));
    }

    private double getDouble(String path, double def) {
        String defPath = "arena-settings.default." + path;
        String curPath = "arena-settings." + arenaName + "." + path;
        config.addDefault(defPath, def);
        return config.getDouble(curPath, config.getDouble(defPath));
    }

    private ConfigurationSection getSection(String path, Map<String, Object> def) {
        String defPath = "arena-settings.default." + path;
        String curPath = "arena-settings." + arenaName + "." + path;

        ConfigurationSection section = config.getConfigurationSection(curPath);
        if (section != null) {
            return section;
        }

        section = config.getConfigurationSection(defPath);
        if (section == null) {
            config.createSection(defPath, def);
            section = config.getConfigurationSection(defPath);
        }

        return section;
    }

    private List<Map<?, ?>> getMapList(String path, List<Map<String, Object>> def) {
        String defPath = "arena-settings.default." + path;
        String curPath = "arena-settings." + arenaName + "." + path;
        config.addDefault(defPath, def);
        if (config.isSet(curPath)) {
            return config.getMapList(curPath);
        }
        return config.getMapList(defPath);
    }

    public void load() {
        disableHunger = getBoolean("disable-hunger", true);
        minPlayers = getInt("min-players", 2);
        killMoneyMax = getDouble("kill-money.max", -1);
        killMoneyMin = getDouble("kill-money.min", -1);
        rewards = loadRewards("rewards", DEFAULT_REWARDS);
        inventory = loadInventory("inventory", DEFAULT_INVENTORY);
        headArmour = loadItem("armour.head", DEFAULT_HEAD_ARMOUR);
        bodyArmour = loadItem("armour.body", DEFAULT_BODY_ARMOUR);
        legArmour = loadItem("armour.legs", DEFAULT_LEG_ARMOUR);
        bootArmour = loadItem("armour.boots", DEFAULT_BOOT_ARMOUR);
    }

    private ItemStack loadItem(String path, ItemStack def) {
        ConfigurationSection section = getSection(path, serializeItem(def));
        return deserializeItem(section.getValues(true));
    }

    private List<ItemStack> loadInventory(String path, List<ItemStack> def) {
        List<Map<?, ?>> items = getMapList(path, serializeItems(def));
        return items.stream()
                .map(this::deserializeItem)
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> serializeItems(List<ItemStack> items) {
        return items.stream()
                .map(this::serializeItem)
                .collect(Collectors.toList());
    }

    private List<Reward> loadRewards(String path, List<Reward> def) {
        List<Map<?, ?>> rewards = getMapList(path, serializeRewards(def));
        List<Reward> target = new ArrayList<>(rewards.size());
        for (Map<?, ?> reward : rewards) {
            parseReward(reward).ifPresent(target::add);
        }
        return target;
    }

    private Optional<Reward> parseReward(Map<?, ?> data) {
        Optional<RewardType> type = GenericUtils.getEnum(RewardType.class, data, "type");
        if (!type.isPresent()) {
            return Optional.empty();
        }

        switch (type.get()) {
            case COMMAND:
                return parseCommandReward(data);
            case ITEM:
                return parseItemReward(data);
            case MONEY:
                // TODO: Implement money reward.
        }
        return parseItemReward(data);
    }

    private Optional<Reward> parseItemReward(Map<?, ?> data) {
        return Optional.of(new ItemReward(ItemFactory.deserialize(data)));
    }

    private Optional<Reward> parseCommandReward(Map<?, ?> data) {
        Optional<String> command = GenericUtils.getString(data, "command");
        if (!command.isPresent()) {
            return Optional.empty();
        }

        CommandReward.SenderType sender = GenericUtils.getEnum(CommandReward.SenderType.class, data, "sender")
                .orElse(CommandReward.SenderType.CONSOLE);
        return Optional.of(new CommandReward(command.get(), sender));
    }

    private List<Map<String, Object>> serializeRewards(List<Reward> rewards) {
        return rewards.stream()
                .map(ConfigurationSerializable::serialize)
                .collect(Collectors.toList());
    }

    private Map<String, Object> serializeItem(ItemStack item) {
        Map<String, Object> target = new HashMap<>();
        target.put("material", item.getType().toString().toLowerCase());
        target.put("data", item.getDurability());
        target.put("amount", item.getAmount());
        target.put("name", item.getItemMeta().getDisplayName());
        target.put("lore", item.getItemMeta().getLore());
        for (Map.Entry<Enchantment, Integer> entry : item.getItemMeta().getEnchants().entrySet()) {
            target.put("enchantments." + entry.getKey().toString().toLowerCase(), entry.getValue());
        }
        return target;
    }

    private ItemStack deserializeItem(Map<?, ?> data) {
        ItemFactoryBuilder builder = new ItemFactoryBuilder();
        GenericUtils.getEnum(Material.class, data, "material").ifPresent(builder::material);
        GenericUtils.getInt(data, "data").ifPresent(b -> builder.data(b.byteValue()));
        GenericUtils.getInt(data, "amount").ifPresent(builder::min);
        GenericUtils.getString(data, "name").ifPresent(builder::name);
        GenericUtils.getList(data, "lore").ifPresent(l -> builder.lore(GenericUtils.castList(String.class, l)));
        GenericUtils.getMap(data, "enchantments").ifPresent(m -> builder.enchantments(GenericUtils.parseEnchantments(m)));
        return builder.build().forceCreate();
    }
}
