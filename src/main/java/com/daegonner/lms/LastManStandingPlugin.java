package com.daegonner.lms;

import com.daegonner.lms.listener.PlayerListener;
import com.daegonner.lms.model.*;
import com.daegonner.lms.settings.Settings;
import com.daegonner.lms.task.GameTask;
import com.google.common.collect.ImmutableList;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LastManStandingPlugin extends JavaPlugin {

    private static final ImmutableList<Class<? extends Model>> DATABASE_CLASSES = ImmutableList.of(
            WorldModel.class,
            BlockPosModel.class,
            RegionModel.class,
            ArenaModel.class,
            EntityPosModel.class,
            ArenaSpawnModel.class
    );

    private final Settings settings = new Settings(this);
    private final ArenaManager arenaManager = new ArenaManager(this);
    private final GameTask gameTask = new GameTask(this);
    private final ImmutableList<Listener> listeners = ImmutableList.of(
            new PlayerListener(this)
    );

    public Settings getSettings() {
        return settings;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameTask getGameTask() {
        return gameTask;
    }

    @Override
    public void onEnable() {
        if (setupSettings())
            return;

        setupDatabase();
        arenaManager.setup();
        gameTask.runTaskTimer(this, 1, 1);
        listeners.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    /**
     * Performs the settings setup process.
     *
     * @return {@code true} if the process encountered an error.
     */
    private boolean setupSettings() {
        try {
            settings.load();
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().log(Level.SEVERE, "===============");
            getLogger().log(Level.SEVERE, "");
            getLogger().log(Level.SEVERE, "There was an issue with loading the configuration!");
            getLogger().log(Level.SEVERE, "Check the stack trace below to diagnose the problem.");
            getLogger().log(Level.SEVERE, "The issue is most likely due to incorrect syntax.");
            getLogger().log(Level.SEVERE, "");
            getLogger().log(Level.SEVERE, "===============");
            getLogger().log(Level.SEVERE, "Error stack trace: ", e);
            getLogger().log(Level.SEVERE, "Please correct this issue before plugin use.");
            getLogger().log(Level.SEVERE, "Disabling plugin . . .");
            getServer().getPluginManager().disablePlugin(this);
            return true;
        }
        return false;
    }

    private void setupDatabase() {
        try {
            getDatabase().find(ArenaSpawnModel.class).findRowCount();
        } catch (PersistenceException ex) {
            System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<>(DATABASE_CLASSES);
    }
}
