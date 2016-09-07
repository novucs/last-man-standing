package net.novucs.lms.model;

import com.avaje.ebean.validation.NotNull;
import net.novucs.lms.LastManStandingPlugin;
import org.bukkit.Location;

import javax.persistence.*;
import java.util.Objects;

/**
 * A representation of a {@link Location} to be stored in the LMS database.
 */
@Entity
@Table(name = "entity_pos")
@UniqueConstraint(columnNames = {"world_id", "x", "y", "z", "yaw", "pitch"})
public class EntityPosModel implements Model {

    @Id
    @GeneratedValue
    private int id;

    @JoinColumn(name = "world_id")
    @ManyToOne
    private WorldModel world;

    @NotNull
    private double x;

    @NotNull
    private double y;

    @NotNull
    private double z;

    @NotNull
    private float yaw;

    @NotNull
    private float pitch;

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
     * Gets the world.
     *
     * @return the world.
     */
    public WorldModel getWorld() {
        return world;
    }

    /**
     * Sets the world.
     *
     * @param world the world.
     */
    public void setWorld(WorldModel world) {
        this.world = world;
    }

    /**
     * Gets the X coordinate.
     *
     * @return the X coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the X coordinate.
     *
     * @param x the X coordinate.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the Y coordinate.
     *
     * @return the Y coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the Y coordinate.
     *
     * @param y the Y coordinate.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the Z coordinate.
     *
     * @return the Z coordinate.
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the Z coordinate.
     *
     * @param z the Z coordinate.
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Gets the yaw.
     *
     * @return the yaw.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the yaw.
     *
     * @param yaw the yaw.
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Gets the pitch.
     *
     * @return the pitch.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the pitch.
     *
     * @param pitch the pitch.
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Gets or creates a new {@link EntityPosModel} from the LMS database.
     *
     * @param plugin the {@link LastManStandingPlugin} plugin instance.
     * @param world  the world.
     * @param x      the X coordinate.
     * @param y      the Y coordinate.
     * @param z      the Z coordinate.
     * @param yaw    the yaw.
     * @param pitch  the pitch.
     * @return the entity position using these coordinates.
     */
    public static EntityPosModel get(LastManStandingPlugin plugin, WorldModel world, double x, double y, double z,
                                     float yaw, float pitch) {
        EntityPosModel model = plugin.getDatabase().find(EntityPosModel.class).where()
                .eq("world", world).eq("x", x).eq("y", y).eq("z", z).eq("yaw", yaw).eq("pitch", pitch).findUnique();
        if (model == null) {
            model = new EntityPosModel();
            model.setWorld(world);
            model.setX(x);
            model.setY(y);
            model.setZ(z);
            model.setYaw(yaw);
            model.setPitch(pitch);
            plugin.getDatabase().save(model);
        }
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityPosModel that = (EntityPosModel) o;
        return id == that.id &&
                Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0 &&
                Float.compare(that.yaw, yaw) == 0 &&
                Float.compare(that.pitch, pitch) == 0 &&
                Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return "EntityPosModel{" +
                "id=" + id +
                ", world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
