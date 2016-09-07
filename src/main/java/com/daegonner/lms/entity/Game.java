package com.daegonner.lms.entity;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Contains all data for an active LMS game.
 */
public class Game {

    private final Arena arena;
    private final Set<Player> participants;
    private final Set<Player> spectators = new HashSet<>();

    public Game(Arena arena, Set<Player> participants) {
        this.arena = arena;
        this.participants = participants;
    }

    /**
     * Gets the arena in use.
     *
     * @return the arena.
     */
    public Arena getArena() {
        return arena;
    }

    /**
     * Gets all non-dead participants.
     *
     * @return the participants.
     */
    public Set<Player> getParticipants() {
        return participants;
    }

    /**
     * Gets the spectators.
     *
     * @return the spectators.
     */
    public Set<Player> getSpectators() {
        return spectators;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(arena, game.arena) &&
                Objects.equals(participants, game.participants) &&
                Objects.equals(spectators, game.spectators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arena, participants, spectators);
    }

    @Override
    public String toString() {
        return "Game{" +
                "arena=" + arena +
                ", participants=" + participants +
                ", spectators=" + spectators +
                '}';
    }
}
