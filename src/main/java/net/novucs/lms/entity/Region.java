package net.novucs.lms.entity;

import net.novucs.lms.model.BlockPosModel;
import org.bukkit.World;

import java.util.Objects;

/**
 * Pertains two points, declaring a specific area in one world.
 */
public class Region {

    private BlockPosModel max;
    private BlockPosModel min;

    /**
     * Creates a region from two locations, automatically calculates which
     * locations are minimum and maximum.
     *
     * @param loc1 the first location.
     * @param loc2 the second location.
     * @return the new {@link Region}.
     * @throws IllegalArgumentException if locations are in different worlds.
     */
    public static Region create(BlockPosModel loc1, BlockPosModel loc2) {
        Region region = new Region();
        region.setPoints(loc1, loc2);
        return region;
    }

    private Region() {
    }

    /**
     * Gets the maximum location.
     *
     * @return the maximum {@link BlockPosModel}.
     */
    public BlockPosModel getMax() {
        return max;
    }

    /**
     * Gets the minimum block position.
     *
     * @return the minimum {@link BlockPosModel}.
     */
    public BlockPosModel getMin() {
        return min;
    }

    /**
     * Sets both points of the region, initializing it.
     *
     * @param pos1 the first {@link BlockPosModel}.
     * @param pos2 the second {@link BlockPosModel}.
     * @throws IllegalArgumentException if locations are in different worlds.
     */
    public void setPoints(BlockPosModel pos1, BlockPosModel pos2) {
//        if (pos1.getWorld() != pos2.getWorld()) {
//            throw new IllegalArgumentException("Both worlds must be in the same world");
//        }
//
//        // Calculate the maximum and minimum coordinates for the two locations.
//        World world = pos1.getWorld();
//        int maxX = Math.max(pos1.getX(), pos2.getX());
//        int minX = Math.min(pos1.getX(), pos2.getX());
//        int maxY = Math.max(pos1.getY(), pos2.getY());
//        int minY = Math.min(pos1.getY(), pos2.getY());
//        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
//        int minZ = Math.min(pos1.getZ(), pos2.getZ());
//
//        // Set the new locations.
//        max = new BlockPosModel(world, maxX, maxY, maxZ);
//        min = new BlockPosModel(world, minX, minY, minZ);
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
