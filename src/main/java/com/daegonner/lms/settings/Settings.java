package com.daegonner.lms.settings;

import com.daegonner.lms.LastManStandingPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Settings {

    private static final int LATEST_VERSION = 1;

    private final LastManStandingPlugin plugin;

    private FileConfiguration config;
    private File configFile;

    private String lobbyStartMessage;
    private String lobbyCountdownMessage;
    private String gameTeleportedMessage;
    private String gameWarmupMessage;

    private int lobbyStart;
    private int lobbyCountdown;
    private List<Integer> announcementTimes;

    private ArenaSettings defaultArenaSettings;
    private Map<String, ArenaSettings> arenaSettingsMap;

    public static int getLatestVersion() {
        return LATEST_VERSION;
    }

    public LastManStandingPlugin getPlugin() {
        return plugin;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getConfigFile() {
        return configFile;
    }

    public String getLobbyStartMessage() {
        return lobbyStartMessage;
    }

    public String getLobbyCountdownMessage() {
        return lobbyCountdownMessage;
    }

    public String getGameTeleportedMessage() {
        return gameTeleportedMessage;
    }

    public String getGameWarmupMessage() {
        return gameWarmupMessage;
    }

    public int getLobbyStart() {
        return lobbyStart;
    }

    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    public List<Integer> getAnnouncementTimes() {
        return announcementTimes;
    }

    public ArenaSettings getArenaSettings(String arenaName) {
        return arenaSettingsMap.getOrDefault(arenaName, defaultArenaSettings);
    }

    public Settings(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    private void set(String path, Object val) {
        config.set(path, val);
    }

    private boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path);
    }

    private int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path);
    }

    private long getLong(String path, long def) {
        config.addDefault(path, def);
        return config.getLong(path);
    }

    private double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path);
    }

    private String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path);
    }

    private ConfigurationSection getOrCreateSection(String key) {
        return config.getConfigurationSection(key) == null ?
                config.createSection(key) : config.getConfigurationSection(key);
    }

    private ConfigurationSection getOrDefaultSection(String key) {
        return config.getConfigurationSection(key).getKeys(false).isEmpty() ?
                config.getDefaults().getConfigurationSection(key) : config.getConfigurationSection(key);
    }

    private <T> List<?> getList(String key, List<T> def) {
        config.addDefault(key, def);
        return config.getList(key, config.getList(key));
    }

    private <E> List<E> getList(String key, List<E> def, Class<E> type) {
        try {
            return castList(type, getList(key, def));
        } catch (ClassCastException e) {
            return def;
        }
    }

    private <E> List<E> castList(Class<? extends E> type, List<?> toCast) throws ClassCastException {
        return toCast.stream().map(type::cast).collect(Collectors.toList());
    }

    private String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void load() throws IOException, InvalidConfigurationException {
        // Create then load the configuration and file.
        configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
        configFile.getParentFile().mkdirs();
        configFile.createNewFile();

        config = new YamlConfiguration();
        config.load(configFile);

        lobbyStartMessage = format(getString("messages.lobby-start", "&eLMS lobby is now available to join! &d/lms join"));
        lobbyCountdownMessage = format(getString("messages.lobby-countdown", "&eLMS will start in &d{time}&e. Join with: &d/lms join"));
        gameTeleportedMessage = format(getString("messages.game-teleported", "&eYou have been teleported into LMS"));
        gameWarmupMessage = format(getString("messages.game-warmup", "&eProtection ends in &d{time}"));

        lobbyStart = getInt("settings.lobby-start", 10800);
        lobbyCountdown = getInt("settings.lobby-countdown", 300);
        announcementTimes = getList("settings.announcement-times",
                Arrays.asList(1, 2, 3, 4, 5, 10, 30, 60, 120, 300, 600, 900, 1800), Integer.class);
        Collections.sort(announcementTimes);

        for (String arenaName : config.getConfigurationSection("arena-settings").getKeys(false)) {
            arenaSettingsMap.put(arenaName, new ArenaSettings(config, arenaName));
        }

        defaultArenaSettings = arenaSettingsMap.getOrDefault("default", new ArenaSettings(config, "default"));
        defaultArenaSettings.load();
        arenaSettingsMap.values().forEach(ArenaSettings::load);

        // Load all configuration values into memory.
        int version = getInt("config-version", 0);

        // Update the configuration file if it is outdated.
        if (version < LATEST_VERSION) {
            // Update header and all config values.
            config.options().header(getDocumentation());
            config.options().copyDefaults(true);
            set("config-version", LATEST_VERSION);

            // Save the config.
            config.save(configFile);
            plugin.getLogger().info("Configuration file has been successfully updated.");
        }
    }

    public String getDocumentation() {
        Scanner scanner = new Scanner(plugin.getResource("readme.txt")).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
