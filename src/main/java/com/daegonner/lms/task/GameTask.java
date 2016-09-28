package com.daegonner.lms.task;

import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;
import com.daegonner.lms.entity.Game;
import com.daegonner.lms.entity.Lobby;
import com.daegonner.lms.model.LobbyScheduleModel;
import com.daegonner.lms.settings.ArenaSettings;
import com.daegonner.lms.util.DurationUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class GameTask extends BukkitRunnable {

    private final LastManStandingPlugin plugin;
    private Lobby lobby;
    private Game game;
    private long nextLobby;
    private long lobbyStart;
    private int lastCountdown;

    public Lobby getLobby() {
        return lobby;
    }

    public Game getGame() {
        return game;
    }

    public long getNextLobby() {
        return nextLobby;
    }

    public void setNextLobby(long nextLobby) {
        this.nextLobby = nextLobby;
    }

    public GameTask(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (hasGame()) {
            game.pulse();
            return;
        }

        if (plugin.getArenaManager().getArenas().isEmpty()) {
            if (hasLobby()) {
                closeLobby();
            }
            return;
        }

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
     * Checks if the task has an active game.
     *
     * @return {@code true} if there is an active game.
     */
    public boolean hasGame() {
        return game != null;
    }

    /**
     * Checks if the task has an active lobby.
     *
     * @return {@code true} if there is an active lobby.
     */
    public boolean hasLobby() {
        return lobby != null;
    }

    /**
     * Checks if it is time to begin a new lobby.
     *
     * @return {@code true} if a new lobby should be created.
     */
    private boolean isNextLobbyReady() {
        return System.currentTimeMillis() <= nextLobby;
    }

    /**
     * Creates a new lobby.
     */
    private void createLobby() {
        lobby = new Lobby();
        lobbyStart = System.currentTimeMillis();
        nextLobby = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(plugin.getSettings().getLobbyStart());
        plugin.getExecutorService().execute(() -> {
            LobbyScheduleModel model = LobbyScheduleModel.of(plugin);
            model.setNextLobby(nextLobby);
            plugin.getDatabase().save(model);
        });
        lastCountdown = Integer.MAX_VALUE;
        broadcast(plugin.getSettings().getLobbyStartMessage());
    }

    /**
     * Closes the current lobby.
     */
    private void closeLobby() {
        lobby = null;
        broadcast(plugin.getSettings().getLobbyCancelledMessage());
    }

    /**
     * Checks if the current lobby countdown is finished.
     *
     * @return {@code true} if the countdown is finished.
     */
    private boolean isCountdownFinished() {
        return System.currentTimeMillis() - lobbyStart > TimeUnit.SECONDS.toMillis(plugin.getSettings().getLobbyCountdown());
    }

    /**
     * Attempts to start a new game for the active lobby.
     */
    private void startGame() {
        Optional<Arena> arena = lobby.getHighestVotedArena();
        if (!arena.isPresent()) {
            arena = plugin.getArenaManager().getRandomArena();
            if (!arena.isPresent()) {
                return;
            }
        }

        ArenaSettings settings = plugin.getSettings().getArenaSettings(arena.get().getName());

        if (lobby.getPlayerQueue().size() < settings.getMinPlayers()) {
            broadcast(plugin.getSettings().getLobbyFailedPlayersMessage());
            lobby = null;
            return;
        }

        game = new Game(arena.get(), lobby.getPlayerQueue());
        lobby = null;
        game.start(settings);
        broadcast(plugin.getSettings().getGameTeleportedMessage());
    }

    /**
     * Sends messages in chat for the active lobby countdown.
     */
    private void countdown() {
        long remaining = TimeUnit.MILLISECONDS.toSeconds(getRemainingCountdown());

        for (int time : plugin.getSettings().getAnnouncementTimes()) {
            if (time < remaining && lastCountdown > time) {
                broadcast(plugin.getSettings().getLobbyCountdownMessage().replace("{time}",
                        DurationUtils.format((int) remaining)));
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
        return lobbyStart - System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(plugin.getSettings().getLobbyCountdown());
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
