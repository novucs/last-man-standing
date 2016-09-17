package com.daegonner.lms;

import com.daegonner.lms.model.*;
import com.google.common.collect.ImmutableList;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

public class LastManStandingPlugin extends JavaPlugin {

    private static final ImmutableList<Class<? extends Model>> DATABASE_CLASSES = ImmutableList.of(
            WorldModel.class,
            BlockPosModel.class,
            RegionModel.class,
            ArenaModel.class,
            EntityPosModel.class,
            ArenaSpawnModel.class
    );

    private final ArenaManager arenaManager = new ArenaManager(this);

    @Override
    public void onEnable() {
        setupDatabase();
        arenaManager.setup();
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