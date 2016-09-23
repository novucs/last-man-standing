package com.daegonner.lms.reward;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * When granted executes a command via either console or the player who won.
 */
public class CommandReward implements Reward {

    private final String command;
    private final SenderType senderType;

    public CommandReward(String command, SenderType senderType) {
        this.command = command;
        this.senderType = senderType;
    }

    @Override
    public void grant(Player player, Inventory inventory) {
        CommandSender sender = getSender(player);
        player.getServer().dispatchCommand(sender, command);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> target = new HashMap<>();
        target.put("type", RewardType.COMMAND.name().toLowerCase());
        target.put("command", command);
        target.put("sender", senderType.name().toLowerCase());
        return target;
    }

    /**
     * Gets the sender for this reward.
     *
     * @param player the {@link Player} affected.
     * @return the sender.
     */
    private CommandSender getSender(Player player) {
        switch (senderType) {
            case CONSOLE:
                return player.getServer().getConsoleSender();
            case PLAYER:
                return player;
        }
        return player.getServer().getConsoleSender();
    }

    public enum SenderType {

        CONSOLE,
        PLAYER

    }
}
