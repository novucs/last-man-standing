package com.daegonner.lms.command;

import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;
import com.daegonner.lms.entity.ArenaSpawn;
import com.daegonner.lms.entity.Region;
import com.daegonner.lms.model.*;
import com.daegonner.lms.util.DurationUtils;
import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AdminCommands {

    private final LastManStandingPlugin plugin;

    public AdminCommands(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = "start", desc = "Start an LMS event")
    @Require("lms.start")
    public void start(CommandSender sender) {
        if (plugin.getGameTask().hasLobby() || plugin.getGameTask().hasLobby()) {
            sender.sendMessage(plugin.getSettings().getGameRunningMessage());
            return;
        }

        plugin.getGameTask().createLobby();
    }

    @Command(aliases = "stop", desc = "Stop an LMS event")
    @Require("lms.stop")
    public void stop(CommandSender sender) {
        if (plugin.getGameTask().hasGame()) {
            plugin.getGameTask().getGame().stop();
        }

        if (plugin.getGameTask().hasLobby()) {
            plugin.getGameTask().closeLobby();
        }
    }

    @Command(aliases = "reload", desc = "Reload the configuration")
    @Require("lms.reload")
    public void reload(CommandSender sender) {
        plugin.setupSettings();
        sender.sendMessage(plugin.getSettings().getReloadMessage());
    }

    @Command(aliases = "create", usage = "<name>", desc = "Create a new arena")
    @Require("lms.create")
    public void create(CommandSender sender, String name) {
        // Disallow names above the maximum storage limit.
        if (name.length() > 30) {
            sender.sendMessage(plugin.getSettings().getArenaNameSizeMessage());
            return;
        }

        // Disallow creating arenas of the same name.
        if (plugin.getArenaManager().getArenas().containsKey(name.toLowerCase())) {
            sender.sendMessage(plugin.getSettings().getArenaAlreadyExistsMessage());
            return;
        }

        // Attempt to get the region selected by the sender.
        Optional<Region> region = getRegion(sender);
        if (!region.isPresent()) {
            return;
        }

        Arena arena = new Arena(name, region.get());
        plugin.getArenaManager().getArenas().put(name.toLowerCase(), arena);

        // Save arena to the database and send confirmation message asynchronously.
        plugin.getExecutorService().execute(() -> {
            ArenaModel.of(plugin, arena);
            sender.sendMessage(plugin.getSettings().getArenaCreatedMessage().replace("{name}", name));
        });
    }

    @Command(aliases = "delete", usage = "<arena>", desc = "Delete an arena")
    @Require("lms.delete")
    public void delete(CommandSender sender, Arena arena) {
        // Asynchronously delete arena from the database.
        plugin.getExecutorService().execute(() -> {
            ArenaModel model = ArenaModel.of(plugin, arena);
            plugin.getDatabase().delete(model);

            // Delete arena locally through the server thread.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getArenaManager().getArenas().remove(arena.getName().toLowerCase());
                sender.sendMessage(plugin.getSettings().getArenaDeletedMessage());
            });
        });
    }

    @Command(aliases = "rename", usage = "<arena> <name>", desc = "Rename an arena")
    @Require("lms.rename")
    public void rename(CommandSender sender, Arena arena, String name) {
        // Disallow names above the maximum storage limit.
        if (name.length() > 30) {
            sender.sendMessage(plugin.getSettings().getArenaNameSizeMessage());
            return;
        }

        // Disallow renaming arenas to the same name, unless modifying case.
        if (plugin.getArenaManager().getArenas().containsKey(name.toLowerCase()) &&
                !name.equalsIgnoreCase(arena.getName())) {
            sender.sendMessage(plugin.getSettings().getArenaAlreadyExistsMessage());
            return;
        }

        // Remove the arena from the arena manager.
        plugin.getArenaManager().getArenas().remove(arena.getName().toLowerCase());
        plugin.getArenaManager().getArenas().put(name.toLowerCase(), arena);

        // Save arena to the database asynchronously.
        plugin.getExecutorService().execute(() -> {
            ArenaModel model = ArenaModel.of(plugin, arena);
            model.setName(name);
            plugin.getDatabase().save(model);

            // Modify the arena name and send confirmation message on the server thread.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                arena.setName(name);
                sender.sendMessage(plugin.getSettings().getArenaRenamedMessage().replace("{name}", name));
            });
        });
    }

    @Command(aliases = "schedule", usage = "<seconds>", desc = "Schedule when the next lobby starts")
    @Require("lms.schedule")
    public void schedule(CommandSender sender, Integer seconds) {
        // Calculate when the next lobby should run,
        long nextLobby = TimeUnit.SECONDS.toMillis(seconds) + System.currentTimeMillis();

        // Asynchronously update the database with the new time.
        plugin.getExecutorService().execute(() -> {
            LobbyScheduleModel model = LobbyScheduleModel.of(plugin);
            model.setNextLobby(nextLobby);
            plugin.getDatabase().save(model);

            // Locally update the new time on the server thread.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getGameTask().setNextLobby(nextLobby);
                sender.sendMessage(plugin.getSettings().getLobbyScheduledMessage()
                        .replace("{time}", DurationUtils.format(seconds)));
            });
        });
    }

    @Command(aliases = "setarea", usage = "<area>", desc = "Set the area of an arena with WorldEdit selection")
    @Require("lms.setarea")
    public void setarea(CommandSender sender, Arena arena) {
        // Do nothing if sender has not selected a region.
        Optional<Region> regionOptional = getRegion(sender);
        if (!regionOptional.isPresent()) {
            return;
        }

        Region region = regionOptional.get();

        // Update and save the region to the database asynchronously.
        plugin.getExecutorService().execute(() -> {
            RegionModel regionModel = RegionModel.of(plugin, arena.getRegion());
            regionModel.setMax(BlockPosModel.of(plugin, region.getMax()));
            regionModel.setMin(BlockPosModel.of(plugin, region.getMin()));
            plugin.getDatabase().save(regionModel);

            // Update local region and send confirmation message on the main server thread.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                arena.getRegion().setPoints(region.getMax(), region.getMin());
                sender.sendMessage(plugin.getSettings().getArenaRegionUpdatedMessage());
            });
        });
    }

    @Command(aliases = "addspawn", usage = "<arena>", desc = "Adds a spawn to an arena")
    @Require("lms.addspawn")
    public void addspawn(CommandSender sender, Arena arena) {
        // Disallow non players to set arena spawns.
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getSettings().getPlayerOnlyCommandMessage());
            return;
        }

        // Disallow spawns outside the arena.
        Player player = (Player) sender;
        if (!arena.getRegion().isInside(player.getLocation())) {
            sender.sendMessage(plugin.getSettings().getArenaLocationInvalidMessage());
            return;
        }

        // Asynchronously create and save an arena spawn to the database.
        plugin.getExecutorService().execute(() -> {
            ArenaSpawn spawn = ArenaSpawn.of(player.getLocation());
            ArenaSpawnModel.of(plugin, arena, spawn);

            // Add the spawn to the arena and send confirmation message on the server thread.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                arena.getSpawns().add(spawn);
                player.sendMessage(plugin.getSettings().getArenaSpawnCreatedMessage());
            });
        });
    }

    @Command(aliases = "delspawn", usage = "<arena> <spawn id>", desc = "Deletes a spawn from an arena")
    @Require("lms.delspawn")
    public void delspawn(CommandSender sender, Arena arena, Integer spawnId) {
        // Disallow deleting invalid spawns.
        if (arena.getSpawns().size() < spawnId--) {
            sender.sendMessage(plugin.getSettings().getArenaSpawnInvalidMessage());
            return;
        }

        // Locally remove the arena spawn.
        ArenaSpawn spawn = arena.getSpawns().remove((int) spawnId);

        // Asynchronously delete spawn from the database.
        plugin.getExecutorService().execute(() -> {
            ArenaSpawnModel spawnModel = ArenaSpawnModel.of(plugin, arena, spawn);
            plugin.getDatabase().delete(spawnModel);
            sender.sendMessage(plugin.getSettings().getArenaSpawnDeletedMessage());
        });
    }

    private Optional<Region> getRegion(CommandSender sender) {
        // Disallow non players to create arenas.
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getSettings().getPlayerOnlyCommandMessage());
            return Optional.empty();
        }

        // Get the players world edit selection.
        Player player = (Player) sender;
        WorldEditPlugin worldEdit = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        Selection selection = worldEdit.getSelection(player);

        // Disallow non-cuboid selections.
        if (selection == null || !(selection instanceof CuboidSelection)) {
            player.sendMessage(plugin.getSettings().getInvalidSelectionMessage());
            return Optional.empty();
        }

        // Create the new arena locally.
        CuboidSelection cuboid = (CuboidSelection) selection;
        Region region = Region.create(cuboid.getMaximumPoint(), cuboid.getMinimumPoint());
        return Optional.of(region);
    }
}
