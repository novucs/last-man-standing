package com.daegonner.lms.model;

import com.daegonner.lms.entity.Region;

import javax.persistence.*;
import java.util.Objects;

/**
 * A representation of a {@link Region} to be stored in the LMS database.
 */
@Entity
@Table(name = "region")
@UniqueConstraint(columnNames = {"min_block_pos_id", "max_block_pos_id"})
public class RegionModel implements Model {

    @Id
    @GeneratedValue
    private int id;

    @JoinColumn(name = "min_block_pos_id")
    @ManyToOne(optional = false)
    private BlockPosModel min;

    @JoinColumn(name = "max_block_pos_id")
    @ManyToOne(optional = false)
    private BlockPosModel max;

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
     * Gets the minimum block position.
     *
     * @return the minimum block position.
     */
    public BlockPosModel getMin() {
        return min;
    }

    /**
     * Sets the minimum block position.
     *
     * @param min the minimum block position.
     */
    public void setMin(BlockPosModel min) {
        this.min = min;
    }

    /**
     * Gets the maximum block position.
     *
     * @return the maximum block position.
     */
    public BlockPosModel getMax() {
        return max;
    }

    /**
     * Sets the maximum block position.
     *
     * @param max the maximum block position.
     */
    public void setMax(BlockPosModel max) {
        this.max = max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionModel that = (RegionModel) o;
        return id == that.id &&
                Objects.equals(min, that.min) &&
                Objects.equals(max, that.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, min, max);
    }

    @Override
    public String toString() {
        return "RegionModel{" +
                "id=" + id +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
