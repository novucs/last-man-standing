package com.daegonner.lms.task;

import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;
import com.daegonner.lms.entity.Game;
import com.daegonner.lms.entity.Lobby;
import com.daegonner.lms.util.DurationUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class GameTask extends BukkitRunnable {

    private final LastManStandingPlugin plugin;
    private Lobby lobby;
    private Game game;
    private long lastLobby;
    private int lastCountdown;

    public Lobby getLobby() {
        return lobby;
    }

    public GameTask(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!hasLobby()) {
            if (isNextLobbyReady()) {
                createLobby();
            }
            return;
        }

        if (isCountdownFinished()) {
            startGame();
            return;
        }

        countdown();
    }

    /**
     * Checks if the task has an active lobby.
     *
     * @return {@code true} if there is an active lobby.
     */
    private boolean hasLobby() {
        return lobby != null;
    }

    /**
     * Checks if it is time to begin a new lobby.
     *
     * @return {@code true} if a new lobby should be created.
     */
    private boolean isNextLobbyReady() {
        return System.currentTimeMillis() - lastLobby > plugin.getSettings().getLobbyStart();
    }

    /**
     * Creates a new lobby.
     */
    private void createLobby() {
        lobby = new Lobby();
        lastLobby = System.currentTimeMillis();
        lastCountdown = Integer.MAX_VALUE;
        broadcast(plugin.getSettings().getLobbyStartMessage());
    }

    /**
     * Checks if the current lobby countdown is finished.
     *
     * @return {@code true} if the countdown is finished.
     */
    private boolean isCountdownFinished() {
        return System.currentTimeMillis() - lastLobby > plugin.getSettings().getLobbyCountdown();
    }

    /**
     * Attempts to start a new game for the active lobby.
     */
    private void startGame() {
        Arena arena = lobby.getHighestVotedArena();
        game = new Game(arena, lobby.getPlayerQueue());
        lobby = null;
        game.start(plugin.getSettings().getArenaSettings(arena.getName()));
        broadcast(plugin.getSettings().getGameTeleportedMessage());
    }

    /**
     * Sends messages in chat for the active lobby countdown.
     */
    private void countdown() {
        long remaining = getRemainingCountdown();

        for (int time : plugin.getSettings().getAnnouncementTimes()) {
            if (time < remaining && lastCountdown > time) {
                broadcast(plugin.getSettings().getLobbyCountdownMessage().replace("{time}",
                        DurationUtils.format((int) TimeUnit.MILLISECONDS.toSeconds(remaining))));
                return;
            }
        }
    }

    /**
     * Gets the remaining duration in millis for the active lobby countdown.
     *
     * @return the remaining countdown.
     */
    private long getRemainingCountdown() {
        return lastLobby - System.currentTimeMillis() + plugin.getSettings().getLobbyCountdown();
    }

    /**
     * Attempts to broadcast a message if it is not empty.
     *
     * @param message the message to send.
     */
    private void broadcast(String message) {
        if (!message.isEmpty()) {
            plugin.getServer().broadcastMessage(message);
        }
    }
}
