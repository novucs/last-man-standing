package com.daegonner.lms.model;

import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;
import com.daegonner.lms.entity.ArenaSpawn;

import javax.persistence.*;
import java.util.Objects;

/**
 * A representation of a spawn for the {@link ArenaModel} to be stored in the LMS database.
 */
@Entity
@Table(name = "arena_spawn")
@UniqueConstraint(columnNames = {"arena_id", "entity_pos_id"})
public class ArenaSpawnModel implements Model {

    @Id
    @GeneratedValue
    private int id;

    @JoinColumn(name = "arena_id")
    @ManyToOne(optional = false)
    private ArenaModel arena;

    @JoinColumn(name = "entity_pos_id")
    @ManyToOne(optional = false)
    private EntityPosModel entityPos;

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
     * Gets the arena.
     *
     * @return the arena.
     */
    public ArenaModel getArena() {
        return arena;
    }

    /**
     * Sets the arena.
     *
     * @param arena the arena.
     */
    public void setArena(ArenaModel arena) {
        this.arena = arena;
    }

    /**
     * Gets the spawn location.
     *
     * @return the location.
     */
    public EntityPosModel getEntityPos() {
        return entityPos;
    }

    /**
     * Sets the spawn location.
     *
     * @param entityPos the location.
     */
    public void setEntityPos(EntityPosModel entityPos) {
        this.entityPos = entityPos;
    }

    /**
     * Gets or creates a new {@link ArenaSpawnModel} from the database.
     *
     * @param plugin the {@link LastManStandingPlugin} plugin instance.
     * @param arena  the {@link Arena}.
     * @param spawn  the {@link ArenaSpawn}.
     * @return the arena spawn model of the same position and arena.
     */
    public static ArenaSpawnModel of(LastManStandingPlugin plugin, Arena arena, ArenaSpawn spawn) {
        EntityPosModel entityPos = EntityPosModel.of(plugin, spawn);
        ArenaModel arenaModel = ArenaModel.of(plugin, arena);
        ArenaSpawnModel model = plugin.getDatabase()
                .find(ArenaSpawnModel.class)
                .where()
                .eq("arena", arenaModel)
                .eq("entityPos", entityPos)
                .findUnique();

        if (model == null) {
            model = new ArenaSpawnModel();
            model.setArena(arenaModel);
            model.setEntityPos(entityPos);
            plugin.getDatabase().save(model);
        }

        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArenaSpawnModel that = (ArenaSpawnModel) o;
        return id == that.id &&
                Objects.equals(arena, that.arena) &&
                Objects.equals(entityPos, that.entityPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, arena, entityPos);
    }

    @Override
    public String toString() {
        return "ArenaSpawnModel{" +
                "id=" + id +
                ", arena=" + arena +
                ", entityPos=" + entityPos +
                '}';
    }
}
