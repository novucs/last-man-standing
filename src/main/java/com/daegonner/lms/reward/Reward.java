package com.daegonner.lms.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * A prize able to be granted to a player or placed in a chest.
 */
public interface Reward {

    /**
     * Grants this reward to the specified {@link Player} and {@link Inventory}.
     *
     * @param player    the player to give the reward to.
     * @param inventory the inventory to add any items to.
     */
    void grant(Player player, Inventory inventory);

}
