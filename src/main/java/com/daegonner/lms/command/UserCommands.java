package com.daegonner.lms.command;

import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;
import com.daegonner.lms.entity.ArenaSpawn;
import com.daegonner.lms.entity.Game;
import com.daegonner.lms.entity.Lobby;
import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public class UserCommands {

    private final LastManStandingPlugin plugin;

    public UserCommands(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = "join", desc = "Join the lobby")
    @Require("lms.join")
    public void join(CommandSender sender) {
        if (!(sender instanceof Player)) {
            message(sender, plugin.getSettings().getPlayerOnlyCommandMessage());
            return;
        }

        if (!plugin.getGameTask().hasLobby()) {
            message(sender, plugin.getSettings().getLobbyNonExistentMessage());
            return;
        }

        Player player = (Player) sender;
        plugin.getGameTask().getLobby().getPlayerQueue().add(player);
        message(sender, plugin.getSettings().getLobbyJoinedMessage());
    }

    @Command(aliases = "quit", desc = "Quit the game")
    @Require("lms.quit")
    public void quit(CommandSender sender) {
        if (!(sender instanceof Player)) {
            message(sender, plugin.getSettings().getPlayerOnlyCommandMessage());
            return;
        }

        Player player = (Player) sender;

        if (plugin.getGameTask().hasGame()) {
            if (plugin.getGameTask().getGame().exit(player)) {
                message(player, plugin.getSettings().getGameExitMessage());
            } else {
                message(player, plugin.getSettings().getGameExitFailedMessage());
            }
            return;
        }

        if (plugin.getGameTask().hasLobby()) {
            if (plugin.getGameTask().getLobby().getPlayerQueue().remove(player)) {
                message(player, plugin.getSettings().getGameExitMessage());
            } else {
                message(player, plugin.getSettings().getGameExitFailedMessage());
            }
            return;
        }

        message(player, plugin.getSettings().getGameExitFailedMessage());
    }

    @Command(aliases = "spectate", desc = "Spectate the game")
    @Require("lms.spectate")
    public void spectate(CommandSender sender) {
        if (!(sender instanceof Player)) {
            message(sender, plugin.getSettings().getPlayerOnlyCommandMessage());
            return;
        }

        if (!plugin.getGameTask().hasGame()) {
            message(sender, plugin.getSettings().getGameNonExistentMessage());
            return;
        }

        Player player = (Player) sender;
        Game game = plugin.getGameTask().getGame();
        if (game.getSpectators().contains(player)) {
            game.exit(player);
            message(player, plugin.getSettings().getGameExitMessage());
            return;
        }

        if (game.getParticipants().contains(player)) {
            game.exit(player);
            message(player, plugin.getSettings().getGameExitMessage());
            game.addSpectator(player);
            message(player, plugin.getSettings().getGameSpectateMessage());
            return;
        }

        game.addSpectator(player);
        message(player, plugin.getSettings().getGameSpectateMessage());
    }

    @Command(aliases = "list", desc = "List all arenas")
    @Require("lms.list")
    public void list(CommandSender sender, @Optional Integer page) {
        message(sender, plugin.getSettings().getArenaListHeaderMessage());
        int i = 0;
        for (Arena arena : plugin.getArenaManager().getArenas().values()) {
            i++;
            message(sender, plugin.getSettings().getArenaListMessage()
                    .replace("{id}", String.valueOf(i))
                    .replace("{arena}", arena.getName()));
        }
    }

    @Command(aliases = "info", desc = "View information on an arena")
    @Require("lms.info")
    public void info(CommandSender sender, Arena arena) {
        Supplier<String> spawns = () -> {
            StringBuilder target = new StringBuilder();
            int id = 0;
            for (ArenaSpawn spawn : arena.getSpawns()) {
                id++;
                target.append(plugin.getSettings().getArenaInfoSpawnMessage()
                        .replace("{id}", String.valueOf(id))
                        .replace("{x}", String.valueOf(spawn.getX()))
                        .replace("{y}", String.valueOf(spawn.getY()))
                        .replace("{z}", String.valueOf(spawn.getZ()))
                        .replace("{yaw}", String.valueOf(spawn.getYaw()))
                        .replace("{pitch}", String.valueOf(spawn.getPitch()))
                );
                target.append("\n");
            }
            return target.toString();
        };
        message(sender, plugin.getSettings().getArenaInfoMessage()
                .replace("{arena}", arena.getName())
                .replace("{world}", arena.getRegion().getMax().getWorld().getName())
                .replace("{minX}", String.valueOf(arena.getRegion().getMin().getX()))
                .replace("{minY}", String.valueOf(arena.getRegion().getMin().getY()))
                .replace("{minZ}", String.valueOf(arena.getRegion().getMin().getZ()))
                .replace("{maxX}", String.valueOf(arena.getRegion().getMax().getX()))
                .replace("{maxY}", String.valueOf(arena.getRegion().getMax().getY()))
                .replace("{maxZ}", String.valueOf(arena.getRegion().getMax().getZ()))
                .replace("{spawns}", spawns.get())
        );
    }

    @Command(aliases = "vote", desc = "Vote to play an arena while in lobby")
    @Require("lms.vote")
    public void vote(CommandSender sender, Arena arena) {
        if (!(sender instanceof Player)) {
            message(sender, plugin.getSettings().getPlayerOnlyCommandMessage());
            return;
        }

        if (!plugin.getGameTask().hasLobby()) {
            message(sender, plugin.getSettings().getLobbyNonExistentMessage());
            return;
        }

        Player player = (Player) sender;
        Lobby lobby = plugin.getGameTask().getLobby();

        if (!lobby.getPlayerQueue().contains(player)) {
            message(sender, plugin.getSettings().getLobbyNotJoinedMessage());
            return;
        }

        lobby.getArenaVotes().put(player, arena);
    }

    private void message(CommandSender sender, String msg) {
        if (!msg.isEmpty()) {
            sender.sendMessage(msg);
        }
    }
}
