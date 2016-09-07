package net.novucs.lms.entity;

import java.util.ArrayList;
import java.util.List;

public class Arena {

    private final Region region;
    private final List<ArenaSpawn> spawns = new ArrayList<>();

    public Arena(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    public List<ArenaSpawn> getSpawns() {
        return spawns;
    }
}
