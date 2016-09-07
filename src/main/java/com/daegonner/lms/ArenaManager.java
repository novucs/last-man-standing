package com.daegonner.lms;

import com.daegonner.lms.entity.Arena;
import com.daegonner.lms.entity.Region;
import com.daegonner.lms.model.*;
import com.daegonner.lms.entity.ArenaSpawn;
import com.daegonner.lms.entity.BlockPos;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Manages all interactions with any LMS {@link Arena}.
 */
public class ArenaManager {

    private final LastManStandingPlugin plugin;
    private final List<Arena> arenas = new ArrayList<>();
    private boolean initialized = false;

    public ArenaManager(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    public LastManStandingPlugin getPlugin() {
        return plugin;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    /**
     * Initializes the state of this arena manager.
     *
     * @throws IllegalStateException when the manager has already been initialized.
     */
    public synchronized void setup() {
        if (initialized) {
            throw new IllegalStateException("Arena manager is already initialized");
        }

        // Clear all current arenas.
        arenas.clear();

        // Fetch all arena models from the database.
        List<ArenaModel> fetched = plugin.getDatabase().find(ArenaModel.class)
                .fetch("region")
                .fetch("region.max")
                .fetch("region.min")
                .fetch("region.max.world")
                .fetch("region.min.world")
                .fetch("spawns.entityPos")
                .fetch("spawns.entityPos.world")
                .findList();

        // Parse all arena models and add to the arena list.
        arenas.addAll(fetched.stream()
                .map(this::parseArena)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        // Change the state.
        initialized = true;
    }

    /**
     * Parses an arena model into a usable arena.
     *
     * @param model the {@link ArenaModel}.
     * @return the newly parsed {@link Arena} or {@code null} if invalid.
     */
    private Arena parseArena(ArenaModel model) {
        // Parse the region.
        Region region = parseRegion(model.getRegion());

        // Do not parse any more if the region parsed is invalid.
        if (region == null) {
            return null;
        }

        // Create the arena.
        Arena arena = new Arena(model.getName(), region);

        // Parse and add all spawns used by this arena.
        arena.getSpawns().addAll(model.getSpawns().stream()
                .map(this::parseArenaSpawn)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        // Return the newly parsed arena.
        return arena;
    }

    /**
     * Parses a region model into a usable region.
     *
     * @param model the {@link RegionModel}.
     * @return the newly parsed {@link Region} or {@code null} if invalid.
     */
    private Region parseRegion(RegionModel model) {
        // Parse the minimum and maximum block positions.
        BlockPos max = parseBlockPos(model.getMax());
        BlockPos min = parseBlockPos(model.getMin());

        // Do not parse any more if the max or min block positions parsed
        // are invalid.
        if (max == null || min == null) {
            return null;
        }

        // Create a new region from the parsed block positions.
        return Region.create(max, min);
    }

    /**
     * Parses a block position model into a usable block position.
     *
     * @param model the {@link BlockPosModel}.
     * @return the newly parsed {@link BlockPos} or {@code null} if invalid.
     */
    private BlockPos parseBlockPos(BlockPosModel model) {
        // Parse the world.
        World world = parseWorld(model.getWorld());

        // Do not parse any more if the world is invalid.
        if (world == null) {
            return null;
        }

        // Create a new block position from the position model.
        return new BlockPos(world, model.getX(), model.getY(), model.getZ());
    }

    /**
     * Parses an arena spawn model into a usable arena spawn.
     *
     * @param model the {@link ArenaSpawnModel}.
     * @return the newly parsed {@link ArenaSpawn} or {@code null} if invalid.
     */
    private ArenaSpawn parseArenaSpawn(ArenaSpawnModel model) {
        // Parse the world.
        EntityPosModel pos = model.getEntityPos();
        World world = parseWorld(pos.getWorld());

        // Do not parse any more if the world is invalid.
        if (world == null) {
            return null;
        }

        // Create a new arena spawn from the spawn model.
        return new ArenaSpawn(world, pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());
    }

    /**
     * Parses a world model into a usable world.
     *
     * @param model the {@link WorldModel}.
     * @return the newly parsed {@link World} or {@code null} if invalid.
     */
    private World parseWorld(WorldModel model) {
        return plugin.getServer().getWorld(model.getName());
    }
}
