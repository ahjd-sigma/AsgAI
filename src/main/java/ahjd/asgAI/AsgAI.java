package ahjd.asgAI;

import ahjd.asgAI.command.ExampleUsage;
import ahjd.asgAI.command.MobAITabCompleter;
import ahjd.asgAI.utils.CommandRegister;
import org.bukkit.plugin.java.JavaPlugin;

public final class AsgAI extends JavaPlugin {

    @Override
    public void onEnable() {
        MobAIManager.initialize(this);
        getServer().getPluginManager().registerEvents(MobAIManager.getInstance(), this);

        CommandRegister.registerCommand(
                "mobai",
                "Mob AI command",
                new ExampleUsage(),
                new MobAITabCompleter()
        );

        getLogger().info("MobAI Plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MobAI Plugin disabled!");
    }
}