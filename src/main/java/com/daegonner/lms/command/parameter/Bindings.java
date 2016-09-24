package com.daegonner.lms.command.parameter;

import com.daegonner.lms.LastManStandingPlugin;
import com.daegonner.lms.entity.Arena;
import com.sk89q.intake.parametric.ParameterException;
import com.sk89q.intake.parametric.argument.ArgumentStack;
import com.sk89q.intake.parametric.binding.BindingBehavior;
import com.sk89q.intake.parametric.binding.BindingHelper;
import com.sk89q.intake.parametric.binding.BindingMatch;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Bindings extends BindingHelper {

    private final LastManStandingPlugin plugin;

    public Bindings(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @BindingMatch(type = CommandSender.class, behavior = BindingBehavior.PROVIDES)
    public CommandSender getCommandSender(ArgumentStack context) throws ParameterException {
        CommandSender sender = context.getContext().getLocals().get(CommandSender.class);

        if (sender == null) {
            throw new ParameterException("No command sender.");
        }

        return sender;
    }

    @BindingMatch(type = Player.class, behavior = BindingBehavior.CONSUMES, consumedCount = 1)
    public Player getPlayer(ArgumentStack context) throws ParameterException {
        String name = context.next();
        Player player = plugin.getServer().getPlayerExact(name);

        if (player == null) {
            throw new ParameterException("Player is not online.");
        }

        return player;
    }

    @BindingMatch(type = Arena.class, behavior = BindingBehavior.CONSUMES, consumedCount = 1)
    public Arena getArena(ArgumentStack context) throws ParameterException {
        String name = context.next();
        Arena arena = plugin.getArenaManager().getArenas().get(name);

        if (arena == null) {
            throw new ParameterException("No arena found by that name.");
        }

        return arena;
    }
}
