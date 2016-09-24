package com.daegonner.lms.command;

import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;
import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import org.bukkit.command.CommandSender;

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
    }

    @Command(aliases = "delete", usage = "<arena>", desc = "Delete an arena")
    @Require("lms.delete")
    public void delete(CommandSender sender, Arena arena) {
    }

    @Command(aliases = "rename", usage = "<arena> <name>", desc = "Rename an arena")
    @Require("lms.rename")
    public void rename(CommandSender sender, Arena arena, String name) {
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
