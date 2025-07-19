package ahjd.asgAI;

import ahjd.asgAI.commands.CustomMobCommand;
import ahjd.asgAI.custommobs.CustomMobManager;
import ahjd.asgAI.utils.CommandRegister;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class AsgAI extends JavaPlugin {
    private static AsgAI instance;
    private CustomMobManager customMobManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize custom mob system
        this.customMobManager = new CustomMobManager(this);

        // Register commands
        registerCommands();
        
        // Start cleanup task
        startCleanupTask();

        getLogger().info("AsgAI Plugin enabled with Custom Mob System!");
    }

    @Override
    public void onDisable() {
        if (customMobManager != null) {
            // Clean up any remaining tasks
            customMobManager.cleanup();
        }
        getLogger().info("AsgAI Plugin disabled!");
    }

    private void registerCommands() {
        CustomMobCommand mobCommandHandler = new CustomMobCommand(customMobManager, this);
        CommandRegister.registerCommand(
                "custommob",
                "Custom AI Mob management commands",
                mobCommandHandler,
                mobCommandHandler
        );
    }
    
    private void startCleanupTask() {
        // Run cleanup every 5 minutes
        new BukkitRunnable() {
            @Override
            public void run() {
                if (customMobManager != null) {
                    customMobManager.cleanup();
                }
            }
        }.runTaskTimer(this, 6000L, 6000L); // 5 minutes = 6000 ticks
    }

    public static AsgAI getInstance() {
        return instance;
    }
    
    public CustomMobManager getCustomMobManager() {
        return customMobManager;
    }
}
