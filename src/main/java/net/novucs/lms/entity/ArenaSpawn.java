package net.novucs.lms.entity;

import org.bukkit.World;

import java.util.Objects;

/**
 * A position players are allowed to spawn when sent to an arena.
 */
public class ArenaSpawn {

    private final World world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public ArenaSpawn(World world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Gets the world.
     *
     * @return the world.
     */
    public World getWorld() {
        return world;
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
     * Gets the Y coordinate.
     *
     * @return the Y coordinate.
     */
    public double getY() {
        return y;
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
     * Gets the yaw.
     *
     * @retur the yaw.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Gets the pitch.
     *
     * @return the pitch.
     */
    public float getPitch() {
        return pitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArenaSpawn that = (ArenaSpawn) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0 &&
                Float.compare(that.yaw, yaw) == 0 &&
                Float.compare(that.pitch, pitch) == 0 &&
                Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return "ArenaSpawn{" +
                "world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
