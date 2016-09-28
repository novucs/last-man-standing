package com.daegonner.lms.model;

import com.avaje.ebean.validation.NotNull;
import com.daegonner.lms.LastManStandingPlugin;

import javax.persistence.*;
import java.util.Objects;

/**
 * Stores when the next lobby should be scheduled.
 */
@Entity
@Table(name = "lobby_schedule")
public class LobbyScheduleModel implements Model {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "next_lobby")
    @NotNull
    private long nextLobby;

    /**
     * Gets the id.
     *
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets when the next lobby should run.
     *
     * @return the next lobby.
     */
    public long getNextLobby() {
        return nextLobby;
    }

    /**
     * Sets when the next lobby should run.
     *
     * @param nextLobby the next lobby.
     */
    public void setNextLobby(long nextLobby) {
        this.nextLobby = nextLobby;
    }

    /**
     * Gets or creates a new {@link LobbyScheduleModel} from the database.
     *
     * @param plugin the {@link LastManStandingPlugin} plugin instance.
     * @return the lobby schedule model.
     */
    public static LobbyScheduleModel of(LastManStandingPlugin plugin) {
        LobbyScheduleModel model = plugin.getDatabase()
                .find(LobbyScheduleModel.class)
                .where()
                .eq("id", 1)
                .findUnique();

        if (model == null) {
            model = new LobbyScheduleModel();
            plugin.getDatabase().save(model);
        }

        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyScheduleModel model = (LobbyScheduleModel) o;
        return id == model.id &&
                nextLobby == model.nextLobby;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nextLobby);
    }

    @Override
    public String toString() {
        return "LobbyScheduleModel{" +
                "id=" + id +
                ", nextLobby=" + nextLobby +
                '}';
    }
}
