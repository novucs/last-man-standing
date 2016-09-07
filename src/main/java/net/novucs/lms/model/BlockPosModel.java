package net.novucs.lms.model;

import com.avaje.ebean.validation.NotNull;
import net.novucs.lms.LastManStandingPlugin;
import net.novucs.lms.entity.BlockPos;

import javax.persistence.*;
import java.util.Objects;

/**
 * A representation of a {@link BlockPos} to be stored in the LMS database.
 */
@Entity
@Table(name = "block_pos")
@UniqueConstraint(columnNames = {"world_id", "x", "y", "z"})
public class BlockPosModel implements Model {

    @Id
    @GeneratedValue
    private int id;

    @JoinColumn(name = "world_id")
    @ManyToOne(optional = false)
    private WorldModel world;

    @NotNull
    private int x;

    @NotNull
    private int y;

    @NotNull
    private int z;

    /**
     * Gets the id.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id the id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the world.
     * @return the world.
     */
    public WorldModel getWorld() {
        return world;
    }

    /**
     * Sets the world
     * @param world the world.
     */
    public void setWorld(WorldModel world) {
        this.world = world;
    }

    /**
     * Gets the X coordinate.
     * @return the X coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the X coordinate.
     * @param x the X coordinate.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the Y coordinate.
     * @return the Y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the Y coordinate.
     * @param y the Y coordinate.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the Z coordinate.
     * @return the Z coordinate.
     */
    public int getZ() {
        return z;
    }

    /**
     * Sets the Z coordinate.
     * @param z the Z coordinate.
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * Gets or creates a new {@link BlockPosModel} from the LMS database.
     *
     * @param plugin the {@link LastManStandingPlugin} plugin instance.
     * @param world  the world.
     * @param x      the X coordinate.
     * @param y      the Y coordinate.
     * @param z      the Z coordinate.
     * @return the block position using these coordinates.
     */
    public static BlockPosModel of(LastManStandingPlugin plugin, WorldModel world, int x, int y, int z) {
        BlockPosModel model = plugin.getDatabase().find(BlockPosModel.class).where()
                .eq("world", world).eq("x", x).eq("y", y).eq("z", z).findUnique();
        if (model == null) {
            model = new BlockPosModel();
            model.setWorld(world);
            model.setX(x);
            model.setY(y);
            model.setZ(z);
            plugin.getDatabase().save(model);
        }
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPosModel that = (BlockPosModel) o;
        return x == that.x &&
                y == that.y &&
                z == that.z &&
                Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    @Override
    public String toString() {
        return "BlockPosModel{" +
                "world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
