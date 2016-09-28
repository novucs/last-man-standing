package com.daegonner.lms.listener;

import com.daegonner.lms.LastManStandingPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        }
    }
}
