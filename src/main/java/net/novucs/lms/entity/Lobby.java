package net.novucs.lms.entity;

import org.bukkit.entity.Player;

import java.awt.geom.Area;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Lobby {

    private final Set<Player> playerQueue = new HashSet<>();
    private Area chosenArena;

    /**
     * An unmodifiable set of the player queue.
     *
     * @return the player queue;
     */
    public Set<Player> getPlayerQueue() {
        return playerQueue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lobby lobby = (Lobby) o;
        return Objects.equals(playerQueue, lobby.playerQueue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerQueue);
    }

    @Override
    public String toString() {
        return "Lobby{" +
                "playerQueue=" + playerQueue +
                '}';
    }
}
