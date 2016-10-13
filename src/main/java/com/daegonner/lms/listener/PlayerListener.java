package com.daegonner.lms.listener;

import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PlayerListener implements Listener {

    private final LastManStandingPlugin plugin;

    public PlayerListener(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleDeath(PlayerDeathEvent event) {
        // Do nothing if there is no LMS game running.
        if (!plugin.getGameTask().hasGame() ||
                !plugin.getGameTask().getGame().getParticipants().contains(event.getEntity()) ||
                !plugin.getGameTask().getGame().getSpectators().contains(event.getEntity())) {
            return;
        }

        // Register the player death on the game.
        plugin.getGameTask().getGame().playerDeath(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleQuit(PlayerQuitEvent event) {
        if (plugin.getGameTask().hasGame()) {
            plugin.getGameTask().getGame().exit(event.getPlayer());
        }

        if (plugin.getGameTask().hasLobby()) {
            plugin.getGameTask().getLobby().getPlayerQueue().remove(event.getPlayer());
            plugin.getGameTask().getLobby().getArenaVotes().remove(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void grantReward(BlockPlaceEvent event) {
        // Do nothing if the placed item is not a reward crate.
        ItemStack placed = event.getItemInHand();
        if (placed == null || !isRewardCrate(placed) || !(event.getBlockPlaced().getState() instanceof Chest)) {
            return;
        }

        getArena(placed).ifPresent(arena -> {
            Chest chest = (Chest) event.getBlockPlaced().getState();
            plugin.getSettings().getArenaSettings(arena.getName()).getRewards().forEach(reward -> {
                reward.grant(event.getPlayer(), chest.getInventory());
            });
        });
    }

    /**
     * Gets the {@link Arena} this crate has been rewarded for.
     *
     * @param crate the crate item stack.
     * @return the arena used.
     */
    private Optional<Arena> getArena(ItemStack crate) {
        if (!crate.hasItemMeta() || !crate.getItemMeta().hasLore()) {
            return Optional.empty();
        }

        String arenaLine = LastManStandingPlugin.getRewardCrateLore().get(1).replace("{arena}", "");
        for (String line : crate.getItemMeta().getLore()) {
            if (line.startsWith(arenaLine)) {
                String name = line.substring(arenaLine.length()).toLowerCase();
                return Optional.ofNullable(plugin.getArenaManager().getArenas().get(name));
            }
        }

        return Optional.empty();
    }

    /**
     * Checks if an item stack is a reward crate by comparing their lores.
     *
     * @param stack the {@link ItemStack} to check.
     * @return {@code true} if the item is a reward crate.
     */
    private boolean isRewardCrate(ItemStack stack) {
        return stack.hasItemMeta() && stack.getItemMeta().hasLore() &&
                stack.getItemMeta().getLore().contains(LastManStandingPlugin.getRewardCrateLore().get(0));
    }
}
