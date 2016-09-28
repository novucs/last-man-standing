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

    private String reloadMessage;
    private String lobbyStartMessage;
    private String lobbyCountdownMessage;
    private String lobbyFailedPlayersMessage;
    private String lobbyCancelledMessage;
    private String lobbyNonExistentMessage;
    private String lobbyJoinedMessage;
    private String lobbyScheduledMessage;
    private String gameTeleportedMessage;
    private String gameWarmupMessage;
    private String gameCancelledMessage;
    private String gameCompleteMessage;
    private String playerOnlyCommandMessage;
    private String invalidSelectionMessage;
    private String arenaAlreadyExistsMessage;
    private String arenaCreatedMessage;
    private String arenaRenamedMessage;
    private String arenaNameSizeMessage;
    private String arenaLocationInvalidMessage;
    private String arenaSpawnCreatedMessage;
    private String arenaSpawnInvalidMessage;
    private String arenaSpawnDeletedMessage;
    private String arenaRegionUpdatedMessage;
    private String arenaDeletedMessage;
    private String arenaListHeaderMessage;
    private String arenaListMessage;

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

    public String getReloadMessage() {
        return reloadMessage;
    }

    public String getLobbyStartMessage() {
        return lobbyStartMessage;
    }

    public String getLobbyCountdownMessage() {
        return lobbyCountdownMessage;
    }

    public String getLobbyFailedPlayersMessage() {
        return lobbyFailedPlayersMessage;
    }

    public String getLobbyCancelledMessage() {
        return lobbyCancelledMessage;
    }

    public String getLobbyNonExistentMessage() {
        return lobbyNonExistentMessage;
    }

    public String getLobbyJoinedMessage() {
        return lobbyJoinedMessage;
    }

    public String getLobbyScheduledMessage() {
        return lobbyScheduledMessage;
    }

    public String getGameTeleportedMessage() {
        return gameTeleportedMessage;
    }

    public String getGameWarmupMessage() {
        return gameWarmupMessage;
    }

    public String getGameCancelledMessage() {
        return gameCancelledMessage;
    }

    public String getGameCompleteMessage() {
        return gameCompleteMessage;
    }

    public String getPlayerOnlyCommandMessage() {
        return playerOnlyCommandMessage;
    }

    public String getInvalidSelectionMessage() {
        return invalidSelectionMessage;
    }

    public String getArenaAlreadyExistsMessage() {
        return arenaAlreadyExistsMessage;
    }

    public String getArenaCreatedMessage() {
        return arenaCreatedMessage;
    }

    public String getArenaRenamedMessage() {
        return arenaRenamedMessage;
    }

    public String getArenaNameSizeMessage() {
        return arenaNameSizeMessage;
    }

    public String getArenaLocationInvalidMessage() {
        return arenaLocationInvalidMessage;
    }

    public String getArenaSpawnCreatedMessage() {
        return arenaSpawnCreatedMessage;
    }

    public String getArenaSpawnInvalidMessage() {
        return arenaSpawnInvalidMessage;
    }

    public String getArenaSpawnDeletedMessage() {
        return arenaSpawnDeletedMessage;
    }

    public String getArenaRegionUpdatedMessage() {
        return arenaRegionUpdatedMessage;
    }

    public String getArenaDeletedMessage() {
        return arenaDeletedMessage;
    }

    public String getArenaListHeaderMessage() {
        return arenaListHeaderMessage;
    }

    public String getArenaListMessage() {
        return arenaListMessage;
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

        // Load all configuration values into memory.
        int version = getInt("config-version", 0);

        reloadMessage = format(getString("messages.reload", "&eSettings successfully reloaded"));
        lobbyStartMessage = format(getString("messages.lobby-start", "&eLMS lobby is now available to join! &d/lms join"));
        lobbyCountdownMessage = format(getString("messages.lobby-countdown", "&eLMS will start in &d{time}&e. Join with: &d/lms join"));
        lobbyFailedPlayersMessage = format(getString("messages.lobby-failed-players", "&eLMS unable to start due to too low player interest"));
        lobbyCancelledMessage = format(getString("messages.lobby-cancelled", "&eLMS lobby has been cancelled"));
        lobbyNonExistentMessage = format(getString("messages.lobby-non-existent", "&cNo LMS lobby exists"));
        lobbyJoinedMessage = format(getString("messages.lobby-joined", "&eSuccessfully joined the LMS lobby"));
        lobbyScheduledMessage = format(getString("messages.lobby-scheduled", "&eNext lobby scheduled to run in &d{time}"));
        gameTeleportedMessage = format(getString("messages.game-teleported", "&eYou have been teleported into LMS"));
        gameWarmupMessage = format(getString("messages.game-warmup", "&eProtection ends in &d{time}"));
        gameCancelledMessage = format(getString("messages.game-cancelled", "&eLMS has been cancelled"));
        gameCompleteMessage = format(getString("messages.game-complete", "&d{player}&e has won the LMS!"));
        playerOnlyCommandMessage = format(getString("messages.player-only-command", "&cThis command can only be executed by players"));
        invalidSelectionMessage = format(getString("messages.invalid-selection", "&cPlease create a valid cuboid selection with WorldEdit"));
        arenaAlreadyExistsMessage = format(getString("messages.arena-already-exists", "&cAn arena by that name already exists"));
        arenaCreatedMessage = format(getString("messages.arena-created", "&eArena &d{name}&e created"));
        arenaRenamedMessage = format(getString("messages.arena-renamed", "&eArena renamed to &d{name}"));
        arenaNameSizeMessage = format(getString("messages.arena-name-size", "&cArena name is too long"));
        arenaLocationInvalidMessage = format(getString("messages.arena-location-invalid", "&cYou are not within the arena region"));
        arenaSpawnCreatedMessage = format(getString("messages.arena-spawn-created", "&eSuccessfully created a new arena spawn point"));
        arenaSpawnInvalidMessage = format(getString("messages.arena-spawn-invalid", "&cA spawn by that ID does not exist"));
        arenaSpawnDeletedMessage = format(getString("messages.arena-spawn-deleted", "&eArena spawn successfully deleted"));
        arenaRegionUpdatedMessage = format(getString("messages.arena-region-updated", "&eArena region successfully updated"));
        arenaDeletedMessage = format(getString("messages.arena-deleted", "&eArena successfully deleted"));
        arenaListHeaderMessage = format(getString("messages.arena-list-header", "&e&l -- Arenas -- "));
        arenaListMessage = format(getString("messages.arena-list", "&d{id}&e. {arena}"));

        lobbyStart = getInt("settings.lobby-start", 10800);
        lobbyCountdown = getInt("settings.lobby-countdown", 300);
        announcementTimes = getList("settings.announcement-times",
                Arrays.asList(1, 2, 3, 4, 5, 10, 30, 60, 120, 300, 600, 900, 1800), Integer.class);
        Collections.sort(announcementTimes);

        arenaSettingsMap = new HashMap<>();

        for (String arenaName : getOrCreateSection("arena-settings").getKeys(false)) {
            arenaSettingsMap.put(arenaName, new ArenaSettings(config, arenaName));
        }

        defaultArenaSettings = arenaSettingsMap.compute("default", (k, v) -> {
            if (v == null)
                v = new ArenaSettings(config, "default");
            return v;
        });
        defaultArenaSettings.load();
        arenaSettingsMap.values().forEach(ArenaSettings::load);

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
