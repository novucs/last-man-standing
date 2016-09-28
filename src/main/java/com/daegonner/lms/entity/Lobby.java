package com.daegonner.lms.entity;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * The lobby. Holds the player queue and all map votes before a match begins.
 */
public class Lobby {

    private final Set<Player> playerQueue = new HashSet<>();
    private final Map<Player, Arena> arenaVotes = new HashMap<>();

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
    public Map<Player, Arena> getArenaVotes() {
        return arenaVotes;
    }

    private Map<Arena, Integer> getVoteTotals() {
        Map<Arena, Integer> target = new HashMap<>();
        arenaVotes.forEach((player, arena) -> {
            Integer count = target.getOrDefault(arena, 0);
            target.put(arena, count + 1);
        });
        return target;
    }

    /**
     * Gets the highest voted arena.
     *
     * @return the arena.
     */
    public Optional<Arena> getHighestVotedArena() {
        int voteCount = 0;
        Arena arena = null;
        for (Map.Entry<Arena, Integer> entry : getVoteTotals().entrySet()) {
            if (entry.getValue() > voteCount) {
                voteCount = entry.getValue();
                arena = entry.getKey();
            }
        }
        return Optional.ofNullable(arena);
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
