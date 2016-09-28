package com.daegonner.lms.command;

import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;
import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserCommands {

    private final LastManStandingPlugin plugin;

    public UserCommands(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = "join", desc = "Join the lobby")
    @Require("lms.join")
    public void join(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getSettings().getPlayerOnlyCommandMessage());
            return;
        }

        if (!plugin.getGameTask().hasLobby()) {
            sender.sendMessage(plugin.getSettings().getLobbyNonExistentMessage());
            return;
        }

        Player player = (Player) sender;
        plugin.getGameTask().getLobby().getPlayerQueue().add(player);
        player.sendMessage(plugin.getSettings().getLobbyJoinedMessage());
    }

    @Command(aliases = "quit", desc = "Quit the game")
    @Require("lms.quit")
    public void quit(CommandSender sender) {
    }

    @Command(aliases = "spectate", desc = "Spectate the game")
    @Require("lms.spectate")
    public void spectate(CommandSender sender) {
    }

    @Command(aliases = "list", desc = "List all arenas")
    @Require("lms.list")
    public void list(CommandSender sender, @Optional Integer page) {
    }

    @Command(aliases = "info", desc = "View information on an arena")
    @Require("lms.info")
    public void info(CommandSender sender, Arena arena) {
    }

    @Command(aliases = "vote", desc = "Vote to play an arena while in lobby")
    @Require("lms.vote")
    public void vote(CommandSender sender, Arena arena) {
    }
}
