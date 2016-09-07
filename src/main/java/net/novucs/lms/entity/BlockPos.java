package net.novucs.lms.entity;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

/**
 * Specifies the exact location of a block.
 */
public class BlockPos {

    private final World world;
    private final int x;
    private final int y;
    private final int z;

    public BlockPos(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
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
    public int getX() {
        return x;
    }

    /**
     * Gets the Y coordinate.
     *
     * @return the Y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the Z coordinate.
     *
     * @return the Z coordinate.
     */
    public int getZ() {
        return z;
    }

    /**
     * Creates a new {@link BlockPos} from a {@link Location}.
     *
     * @param loc the location to convert.
     * @return the newly parsed block position.
     */
    public static BlockPos of(Location loc) {
        return new BlockPos(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPos blockPos = (BlockPos) o;
        return x == blockPos.x &&
                y == blockPos.y &&
                z == blockPos.z &&
                Objects.equals(world, blockPos.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    @Override
    public String toString() {
        return "BlockPos{" +
                "world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
