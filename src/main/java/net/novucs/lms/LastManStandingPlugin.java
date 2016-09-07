package net.novucs.lms;

import com.google.common.collect.ImmutableList;
import net.novucs.lms.entity.Arena;
import net.novucs.lms.model.*;
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

    private final List<Arena> arenas = new ArrayList<>();

    @Override
    public void onEnable() {
        setupDatabase();
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
