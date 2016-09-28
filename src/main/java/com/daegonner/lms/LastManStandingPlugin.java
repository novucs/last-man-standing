package com.daegonner.lms;

import com.daegonner.lms.command.AdminCommands;
import com.daegonner.lms.command.UserCommands;
import com.daegonner.lms.command.parameter.Bindings;
import com.daegonner.lms.entity.Arena;
import com.daegonner.lms.listener.PlayerListener;
import com.daegonner.lms.model.*;
import com.daegonner.lms.settings.Settings;
import com.daegonner.lms.task.GameTask;
import com.google.common.collect.ImmutableList;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.context.CommandLocals;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.dispatcher.SimpleDispatcher;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.util.auth.AuthorizationException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static org.bukkit.ChatColor.*;

public class LastManStandingPlugin extends JavaPlugin {

    private static final ImmutableList<Class<? extends Model>> DATABASE_CLASSES = ImmutableList.of(
            WorldModel.class,
            BlockPosModel.class,
            RegionModel.class,
            ArenaModel.class,
            EntityPosModel.class,
            ArenaSpawnModel.class,
            LobbyScheduleModel.class
    );

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Settings settings = new Settings(this);
    private final ArenaManager arenaManager = new ArenaManager(this);
    private final GameTask gameTask = new GameTask(this);
    private final ImmutableList<Listener> listeners = ImmutableList.of(
            new PlayerListener(this)
    );
    private Dispatcher dispatcher;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Settings getSettings() {
        return settings;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameTask getGameTask() {
        return gameTask;
    }

    @Override
    public void onEnable() {
        if (setupSettings())
            return;

        registerCommands();
        setupDatabase();
        arenaManager.setup();
        gameTask.runTaskTimer(this, 1, 1);
        listeners.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        if (gameTask.hasGame()) {
            gameTask.getGame().stop();
        }

        if (gameTask.hasLobby()) {
            gameTask.closeLobby();
        }
    }

    /**
     * Performs the settings setup process.
     *
     * @return {@code true} if the process encountered an error.
     */
    public boolean setupSettings() {
        try {
            settings.load();
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().log(Level.SEVERE, "===============");
            getLogger().log(Level.SEVERE, "");
            getLogger().log(Level.SEVERE, "There was an issue with loading the configuration!");
            getLogger().log(Level.SEVERE, "Check the stack trace below to diagnose the problem.");
            getLogger().log(Level.SEVERE, "The issue is most likely due to incorrect syntax.");
            getLogger().log(Level.SEVERE, "");
            getLogger().log(Level.SEVERE, "===============");
            getLogger().log(Level.SEVERE, "Error stack trace: ", e);
            getLogger().log(Level.SEVERE, "Please correct this issue before plugin use.");
            getLogger().log(Level.SEVERE, "Disabling plugin . . .");
            getServer().getPluginManager().disablePlugin(this);
            return true;
        }
        return false;
    }

    private void setupDatabase() {
        try {
            getDatabase().find(ArenaSpawnModel.class).findRowCount();
        } catch (PersistenceException ex) {
            getLogger().info("Installing database due to first time usage.");
            installDDL();
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<>(DATABASE_CLASSES);
    }

    private void registerCommands() {
        // Initialize a new dispatcher (ensures that any previous one is trashed).
        dispatcher = new SimpleDispatcher();

        // Use the parametric builder to construct CommandCallable objects from annotations.
        ParametricBuilder builder = new ParametricBuilder();

        // Register bindings used to resolve custom type parameters.
        builder.addBinding(new Bindings(this), CommandSender.class, Player.class, Arena.class);

        // Create and register CommandCallable objects from the methods of each passed object.
        builder.registerMethodsAsCommands(dispatcher, new AdminCommands(this));
        builder.registerMethodsAsCommands(dispatcher, new UserCommands(this));

        // The authorizer will test whether the command sender has permission.
        builder.setAuthorizer((locals, permission) -> {
            CommandSender sender = locals.get(CommandSender.class);
            return sender != null && sender.hasPermission(permission);
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Reconstruct the full command message.
        String message = StringUtils.join(args, " ");
        CommandLocals locals = new CommandLocals();

        // The CommandSender is made always available.
        locals.put(CommandSender.class, sender);

        // Used to determine command prefix.
        boolean isConsole = sender == getServer().getConsoleSender();

        try {

            // Execute dispatcher with the fully reconstructed command message.
            dispatcher.call(message, locals, new String[0]);

        } catch (InvalidUsageException e) {

            // Invalid command usage should not be harmful. Print something friendly.
            sender.sendMessage(DARK_AQUA + e.getSimpleUsageString((isConsole ? "" : "/") + "lms "));
            sender.sendMessage(DARK_RED + "Error: " + RED + e.getMessage());

        } catch (AuthorizationException e) {

            // Print friendly message in case of permission failure.
            sender.sendMessage(RED + "Permission denied.");

        } catch (CommandException e) {

            // Everything else is unexpected and should be considered an error.
            throw new RuntimeException(e);

        }

        return true;
    }
}
