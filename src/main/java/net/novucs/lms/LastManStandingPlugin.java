package net.novucs.lms;

import com.google.common.collect.ImmutableList;
import net.novucs.lms.model.*;
import org.bukkit.entity.Player;
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

    @Override
    public void onEnable() {
        setupDatabase();
        test();
        test();
    }

    private void test() {
        System.out.println("trying via main method");
        WorldModel model = getDatabase().find(WorldModel.class).where().eq("name", "world").findUnique();
        if (model == null) {
            model = new WorldModel();
            model.setName("world");
        }

        System.out.println("WORLD MODEL: " + model);
        System.out.println("Getting world");
        WorldModel world = WorldModel.get(this, "world");
        System.out.println("Getting blockpos");
        BlockPosModel pos = BlockPosModel.get(this, world, 1, 2, 3);
        System.out.println("finished");
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
