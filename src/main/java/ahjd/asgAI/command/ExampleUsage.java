package ahjd.asgAI.command;

import ahjd.asgAI.MobAIManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExampleUsage implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("Usage: /mobai <spawn|change> [behavior]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "spawn":
                if (args.length < 2) {
                    player.sendMessage("Usage: /mobai spawn <behavior>");
                    return true;
                }

                // Spawn a zombie with custom behavior
                Zombie zombie = (Zombie) player.getWorld().spawnEntity(
                        player.getLocation(), EntityType.ZOMBIE
                );

                try {
                    MobAIManager.getInstance().applyBehavior(zombie, args[1]);
                    player.sendMessage("Spawned zombie with " + args[1] + " behavior!");
                } catch (Exception e) {
                    player.sendMessage("Error: " + e.getMessage());
                }
                break;

            case "change":
                if (args.length < 2) {
                    player.sendMessage("Usage: /mobai change <behavior>");
                    return true;
                }

                // Change behavior of nearby mobs
                player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10)
                        .stream()
                        .filter(entity -> entity instanceof Mob)
                        .map(entity -> (Mob) entity)
                        .forEach(mob -> {
                            try {
                                MobAIManager.getInstance().changeBehavior(mob, args[1]);
                                player.sendMessage("Changed behavior of " + mob.getType() + " to " + args[1]);
                            } catch (Exception e) {
                                player.sendMessage("Error changing behavior: " + e.getMessage());
                            }
                        });
                break;

            default:
                player.sendMessage("Unknown command!");
                break;
        }

        return true;
    }
}