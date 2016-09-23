package com.daegonner.lms.entity;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A named region in which an LMS {@link Game} can be hosted.
 */
public class Arena {

    private final Region region;
    private final List<ArenaSpawn> spawns = new ArrayList<>();
    private String name;

    public Arena(String name, Region region) {
        this.name = name;
        this.region = region;
    }

    /**
     * Gets the region.
     *
     * @return the region.
     */
    public Region getRegion() {
        return region;
    }

    /**
     * Gets the spawns.
     *
     * @return the spawns.
     */
    public List<ArenaSpawn> getSpawns() {
        return spawns;
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
     * Sets the name
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets a random spawn location.
     *
     * @return the random spawn.
     */
    public Location getRandomSpawn() {
        return spawns.get(ThreadLocalRandom.current().nextInt(spawns.size())).toLocation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return Objects.equals(region, arena.region) &&
                Objects.equals(spawns, arena.spawns) &&
                Objects.equals(name, arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region, spawns, name);
    }

    @Override
    public String toString() {
        return "Arena{" +
                "region=" + region +
                ", spawns=" + spawns +
                ", name='" + name + '\'' +
                '}';
    }
}
