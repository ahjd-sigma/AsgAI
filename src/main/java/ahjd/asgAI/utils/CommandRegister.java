package ahjd.asgAI.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class CommandRegister {
    public static void registerCommand(String name, String description, CommandExecutor executor, TabCompleter completer) {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            Command command = new BukkitCommand(name) {
                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return executor.onCommand(sender, this, label, args);
                }

                @Override
                public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                    return completer != null
                            ? completer.onTabComplete(sender, this, alias, args)
                            : super.tabComplete(sender, alias, args);
                }
            };

            command.setDescription(description);
            command.setAliases(Collections.emptyList());

            commandMap.register(name, command);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}