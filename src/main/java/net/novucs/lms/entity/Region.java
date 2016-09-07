package net.novucs.lms.entity;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

/**
 * Pertains two points, declaring a specific area in one world.
 */
public class Region {

    private BlockPos max;
    private BlockPos min;

    /**
     * Helper method to allow creation of a {@link BlockPos} from two
     * {@link Location} objects.
     *
     * @param pos1 the first {@link Location}.
     * @param pos2 the second {@link Location}.
     * @return the new {@link Region}.
     * @throws IllegalArgumentException if locations are in different worlds.
     */
    public static Region create(Location loc1, Location loc2) {
        return create(BlockPos.of(loc1), BlockPos.of(loc2));
    }

    /**
     * Creates a region from two locations, automatically calculates which
     * locations are minimum and maximum.
     *
     * @param pos1 the first {@link BlockPos}.
     * @param pos2 the second {@link BlockPos}.
     * @return the new {@link Region}.
     * @throws IllegalArgumentException if positions are in different worlds.
     */
    public static Region create(BlockPos pos1, BlockPos pos2) {
        Region region = new Region();
        region.setPoints(pos1, pos2);
        return region;
    }

    private Region() {
    }

    /**
     * Gets the maximum location.
     *
     * @return the maximum {@link BlockPos}.
     */
    public BlockPos getMax() {
        return max;
    }

    /**
     * Gets the minimum block position.
     *
     * @return the minimum {@link BlockPos}.
     */
    public BlockPos getMin() {
        return min;
    }

    /**
     * Sets both points of the region, initializing it.
     *
     * @param pos1 the first {@link BlockPos}.
     * @param pos2 the second {@link BlockPos}.
     * @throws IllegalArgumentException if positions are in different worlds.
     */
    public void setPoints(BlockPos pos1, BlockPos pos2) {
        if (pos1.getWorld() != pos2.getWorld()) {
            throw new IllegalArgumentException("Both worlds must be in the same world");
        }

        // Calculate the maximum and minimum coordinates for the two locations.
        World world = pos1.getWorld();
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int minX = Math.min(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());

        // Set the new locations.
        max = new BlockPos(world, maxX, maxY, maxZ);
        min = new BlockPos(world, minX, minY, minZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return Objects.equals(max, region.max) &&
                Objects.equals(min, region.min);
    }

    @Override
    public int hashCode() {
        return Objects.hash(max, min);
    }

    @Override
    public String toString() {
        return "Region{" +
                "max=" + max +
                ", min=" + min +
                '}';
    }
}
