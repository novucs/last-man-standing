package com.daegonner.lms.command;

import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;
import com.daegonner.lms.entity.Region;
import com.daegonner.lms.model.ArenaModel;
import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands {

    private final LastManStandingPlugin plugin;

    public AdminCommands(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = "start", desc = "Start an LMS event")
    @Require("lms.start")
    public void start(CommandSender sender) {
    }

    @Command(aliases = "stop", desc = "Stop an LMS event")
    @Require("lms.stop")
    public void stop(CommandSender sender) {
    }

    @Command(aliases = "reload", desc = "Reload the configuration")
    @Require("lms.reload")
    public void reload(CommandSender sender) {
    }

    @Command(aliases = "create", usage = "<name>", desc = "Create a new arena")
    @Require("lms.create")
    public void create(CommandSender sender, String name) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getSettings().getPlayerOnlyCommandMessage());
            return;
        }

        if (name.length() > 30) {
            sender.sendMessage(plugin.getSettings().getArenaNameSizeMessage());
            return;
        }

        Player player = (Player) sender;
        WorldEditPlugin worldEdit = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        Selection selection = worldEdit.getSelection(player);

        if (selection == null || !(selection instanceof CuboidSelection)) {
            player.sendMessage(plugin.getSettings().getInvalidSelectionMessage());
            return;
        }

        CuboidSelection cuboid = (CuboidSelection) selection;
        Region region = Region.create(cuboid.getMaximumPoint(), cuboid.getMinimumPoint());
        Arena arena = new Arena(name, region);
        ArenaModel.of(plugin, arena);
        player.sendMessage(plugin.getSettings().getArenaCreatedMessage().replace("{name}", name));
    }

    @Command(aliases = "delete", usage = "<arena>", desc = "Delete an arena")
    @Require("lms.delete")
    public void delete(CommandSender sender, Arena arena) {
    }

    @Command(aliases = "rename", usage = "<arena> <name>", desc = "Rename an arena")
    @Require("lms.rename")
    public void rename(CommandSender sender, Arena arena, String name) {
        if (name.length() > 30) {
            sender.sendMessage(plugin.getSettings().getArenaNameSizeMessage());
            return;
        }
        ArenaModel model = ArenaModel.of(plugin, arena);
        model.setName(name);
        plugin.getDatabase().save(model);
        arena.setName(name);
        sender.sendMessage(plugin.getSettings().getArenaRenamedMessage().replace("{name}", name));
    }

    @Command(aliases = "schedule", usage = "<seconds>", desc = "Schedule when the next lobby starts")
    @Require("lms.schedule")
    public void schedule(CommandSender sender, Integer seconds) {
    }

    @Command(aliases = "setarea", usage = "<area>", desc = "Set the area of an arena with WorldEdit selection")
    @Require("lms.setarea")
    public void setarea(CommandSender sender, Arena arena) {
    }

    @Command(aliases = "addspawn", usage = "<arena>", desc = "Adds a spawn to an arena")
    @Require("lms.addspawn")
    public void addspawn(CommandSender sender, Arena arena) {
    }

    @Command(aliases = "delspawn", usage = "<arena> <spawn>", desc = "Deletes a spawn from an arena")
    @Require("lms.delspawn")
    public void delspawn(CommandSender sender, Arena arena, Integer spawn) {
    }
}
