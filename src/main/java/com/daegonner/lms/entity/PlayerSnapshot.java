package com.daegonner.lms.entity;

import com.google.common.collect.ImmutableList;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * A player snapshot to be stored while participating in an LMS game.
 */
public class PlayerSnapshot {

    private final Player player;
    private final Location location;
    private final ItemStack[] items;
    private final ImmutableList<PotionEffect> potionEffects;
    private final GameMode gameMode;

    public PlayerSnapshot(Player player, Location location, ItemStack[] items,
                          ImmutableList<PotionEffect> potionEffects, GameMode gameMode) {
        this.player = player;
        this.location = location;
        this.items = items;
        this.potionEffects = potionEffects;
        this.gameMode = gameMode;
    }

    public static PlayerSnapshot of(Player player) {
        Location location = player.getLocation();
        ItemStack[] items = player.getInventory().getContents();
        ImmutableList<PotionEffect> potionEffects = ImmutableList.copyOf(player.getActivePotionEffects());
        GameMode gameMode = player.getGameMode();
        return new PlayerSnapshot(player, location, items, potionEffects, gameMode);
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public ImmutableList<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public void restore() {
        player.getActivePotionEffects().clear();
        player.addPotionEffects(potionEffects);
        player.getInventory().clear();
        player.getInventory().setContents(items);
        player.teleport(location);
        player.setGameMode(GameMode.SURVIVAL);
    }
}
