package net.novucs.lms;

import net.novucs.lms.entity.Arena;
import net.novucs.lms.entity.ArenaSpawn;
import net.novucs.lms.entity.BlockPos;
import net.novucs.lms.entity.Region;
import net.novucs.lms.model.*;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArenaManager {

    private final LastManStandingPlugin plugin;
    private final List<Arena> arenas = new ArrayList<>();

    public ArenaManager(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    public LastManStandingPlugin getPlugin() {
        return plugin;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public void setup() {
        arenas.clear();
        List<ArenaModel> fetched = plugin.getDatabase().find(ArenaModel.class)
                .fetch("region")
                .fetch("region.max")
                .fetch("region.min")
                .fetch("region.max.world")
                .fetch("region.min.world")
                .fetch("spawns.entityPos")
                .fetch("spawns.entityPos.world")
                .findList();
        arenas.addAll(fetched.stream()
                .map(this::parseArena)
                .collect(Collectors.toList()));
    }

    private Arena parseArena(ArenaModel model) {
        Arena arena = new Arena(parseRegion(model.getRegion()));
        arena.getSpawns().addAll(model.getSpawns().stream()
                .map(this::parseArenaSpawn)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        return arena;
    }

    private Region parseRegion(RegionModel model) {
        BlockPos max = parseBlockPos(model.getMax());
        BlockPos min = parseBlockPos(model.getMin());
        return Region.create(max, min);
    }

    private BlockPos parseBlockPos(BlockPosModel model) {
        World world = plugin.getServer().getWorld(model.getWorld().getName());
        if (world == null) {
            return null;
        }
        return new BlockPos(world, model.getX(), model.getY(), model.getZ());
    }

    private ArenaSpawn parseArenaSpawn(ArenaSpawnModel model) {
        EntityPosModel pos = model.getEntityPos();
        World world = plugin.getServer().getWorld(pos.getWorld().getName());
        if (world == null) {
            return null;
        }
        return new ArenaSpawn(world, pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());
    }
}
