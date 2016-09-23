package com.daegonner.lms.entity;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * The lobby. Holds the player queue and all map votes before a match begins.
 */
public class Lobby {

    private final Set<Player> playerQueue = new HashSet<>();
    private final Map<Arena, Integer> arenaVotes = new HashMap<>();

    /**
     * The current queue of players waiting for the game to start.
     *
     * @return the player queue.
     */
    public Set<Player> getPlayerQueue() {
        return playerQueue;
    }

    /**
     * The total votes for each arena.
     *
     * @return the arena votes.
     */
    public Map<Arena, Integer> getArenaVotes() {
        return arenaVotes;
    }

    /**
     * Gets the highest voted arena.
     *
     * @return the arena.
     */
    public Arena getHighestVotedArena() {
        int voteCount = 0;
        Arena arena = null;
        for (Map.Entry<Arena, Integer> entry : arenaVotes.entrySet()) {
            if (entry.getValue() > voteCount) {
                voteCount = entry.getValue();
                arena = entry.getKey();
            }
        }
        return arena;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lobby lobby = (Lobby) o;
        return Objects.equals(playerQueue, lobby.playerQueue) &&
                Objects.equals(arenaVotes, lobby.arenaVotes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerQueue, arenaVotes);
    }

    @Override
    public String toString() {
        return "Lobby{" +
                "playerQueue=" + playerQueue +
                ", arenaVotes=" + arenaVotes +
                '}';
    }
}
