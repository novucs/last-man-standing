package net.novucs.lms.model;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import net.novucs.lms.LastManStandingPlugin;
import org.bukkit.World;

import javax.persistence.*;
import java.util.Objects;

/**
 * A representation of a {@link World} to be stored in the LMS database.
 */
@Entity
@Table(name = "world")
@UniqueConstraint(columnNames = "name")
public class WorldModel implements Model {

    @Id
    @GeneratedValue
    private int id;

    @Length(max = 30)
    @NotEmpty
    private String name;

    /**
     * Gets the id.
     *
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets a {@link WorldModel} instance from a {@link World}.
     *
     * @param plugin the plugin.
     * @param world  the world.
     * @return the matching instance, new if not already existing.
     */
    public static WorldModel of(LastManStandingPlugin plugin, World world) {
        return of(plugin, world.getName());
    }

    /**
     * Gets a {@link WorldModel} instance from a world name.
     *
     * @param plugin the plugin.
     * @param name   the world name.
     * @return the matching instance, new if not already existing.
     */
    public static WorldModel of(LastManStandingPlugin plugin, String name) {
        WorldModel model = plugin.getDatabase().find(WorldModel.class).where().eq("name", name).findUnique();
        if (model == null) {
            model = new WorldModel();
            model.setName(name);
            plugin.getDatabase().save(model);
        }
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldModel world = (WorldModel) o;
        return Objects.equals(name, world.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "WorldModel{" +
                "name='" + name + '\'' +
                '}';
    }
}
