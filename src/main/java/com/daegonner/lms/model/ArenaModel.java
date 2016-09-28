package com.daegonner.lms.model;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * A representation of a {@link Arena} to be stored in the LMS database.
 */
@Entity
@Table(name = "arena")
@UniqueConstraint(columnNames = "name")
public class ArenaModel implements Model {

    @Id
    @GeneratedValue
    private int id;

    @JoinColumn(name = "region_id")
    @ManyToOne(optional = false)
    private RegionModel region;

    @Length(max = 30)
    @NotEmpty
    private String name;

    @OneToMany(mappedBy = "arena")
    private List<ArenaSpawnModel> spawns;

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
     * Gets the region.
     *
     * @return the region.
     */
    public RegionModel getRegion() {
        return region;
    }

    /**
     * Sets the region.
     *
     * @param region the region.
     */
    public void setRegion(RegionModel region) {
        this.region = region;
    }

    /**
     * Gets the name
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the spawns.
     *
     * @return the spawns.
     */
    public List<ArenaSpawnModel> getSpawns() {
        return spawns;
    }

    /**
     * Sets the spawns.
     *
     * @param spawns the spawns.
     */
    public void setSpawns(List<ArenaSpawnModel> spawns) {
        this.spawns = spawns;
    }

    /**
     * Gets or creates a new {@link ArenaModel} from the database.
     *
     * @param plugin the {@link LastManStandingPlugin} plugin instance.
     * @param arena  the {@link Arena}.
     * @return the arena model of the same name.
     */
    public static ArenaModel of(LastManStandingPlugin plugin, Arena arena) {
        ArenaModel model = plugin.getDatabase()
                .find(ArenaModel.class)
                .where()
                .eq("name", arena.getName())
                .findUnique();

        if (model == null) {
            RegionModel region = RegionModel.of(plugin, arena.getRegion());
            model = new ArenaModel();
            model.setName(arena.getName());
            model.setRegion(region);
            plugin.getDatabase().save(model);
        }

        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArenaModel that = (ArenaModel) o;
        return id == that.id &&
                Objects.equals(region, that.region) &&
                Objects.equals(name, that.name) &&
                Objects.equals(spawns, that.spawns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, region, name, spawns);
    }

    @Override
    public String toString() {
        return "ArenaModel{" +
                "id=" + id +
                ", region=" + region +
                ", name='" + name + '\'' +
                ", spawns=" + spawns +
                '}';
    }
}
