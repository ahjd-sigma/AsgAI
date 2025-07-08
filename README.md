EXAMPLES / USAGES

// ==================== BASIC USAGE EXAMPLES ====================
package com.yourplugin.examples;

import com.yourplugin.mobai.MobAIManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

public class BasicUsageExample extends JavaPlugin {

    @Override
    public void onEnable() {
        // The API is already initialized, just use it!
        
        // Example 1: Spawn a zombie with aggressive behavior
        spawnAggressiveZombie(someLocation);
        
        // Example 2: Change existing mob behavior
        changeMobBehavior(existingMob, "passive");
        
        // Example 3: Create a custom behavior and register it
        registerCustomBehavior();
    }
    
    /**
     * Spawn a zombie that will actively hunt players
     */
    public void spawnAggressiveZombie(Location location) {
        Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
        
        // Apply aggressive behavior - mob will chase and attack players
        MobAIManager.getInstance().applyBehavior(zombie, "aggressive");
        
        // Optional: Customize the mob
        zombie.setCustomName("Aggressive Zombie");
        zombie.setCustomNameVisible(true);
    }
    
    /**
     * Change an existing mob's behavior
     */
    public void changeMobBehavior(Mob mob, String behaviorId) {
        try {
            MobAIManager.getInstance().changeBehavior(mob, behaviorId);
            getLogger().info("Changed mob behavior to: " + behaviorId);
        } catch (Exception e) {
            getLogger().warning("Failed to change mob behavior: " + e.getMessage());
        }
    }
    
    /**
     * Register a custom behavior
     */
    public void registerCustomBehavior() {
        MobAIManager.getInstance().registerBehavior(new GuardBehavior());
    }
}

// ==================== COMMAND INTERFACE ====================
package com.yourplugin.examples;

import com.yourplugin.mobai.MobAIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MobAICommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "spawn":
                return handleSpawn(player, args);
            case "change":
                return handleChange(player, args);
            case "remove":
                return handleRemove(player, args);
            case "info":
                return handleInfo(player, args);
            default:
                showHelp(player);
                return true;
        }
    }
    
    private boolean handleSpawn(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /mobai spawn <mob_type> <behavior>");
            player.sendMessage("Example: /mobai spawn zombie aggressive");
            return true;
        }
        
        try {
            EntityType entityType = EntityType.valueOf(args[1].toUpperCase());
            String behavior = args[2].toLowerCase();
            
            // Spawn the mob
            Mob mob = (Mob) player.getWorld().spawnEntity(player.getLocation(), entityType);
            
            // Apply the behavior
            MobAIManager.getInstance().applyBehavior(mob, behavior);
            
            player.sendMessage("Spawned " + entityType.name() + " with " + behavior + " behavior!");
            
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid mob type or behavior!");
        } catch (Exception e) {
            player.sendMessage("Error: " + e.getMessage());
        }
        
        return true;
    }
    
    private boolean handleChange(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /mobai change <behavior>");
            player.sendMessage("This changes the behavior of mobs you're looking at or nearby");
            return true;
        }
        
        String behavior = args[1].toLowerCase();
        
        // Find nearby mobs or the one player is looking at
        List<Mob> nearbyMobs = player.getWorld().getNearbyEntities(
            player.getLocation(), 10, 10, 10
        ).stream()
        .filter(entity -> entity instanceof Mob)
        .map(entity -> (Mob) entity)
        .collect(Collectors.toList());
        
        if (nearbyMobs.isEmpty()) {
            player.sendMessage("No mobs found nearby!");
            return true;
        }
        
        int changed = 0;
        for (Mob mob : nearbyMobs) {
            try {
                MobAIManager.getInstance().changeBehavior(mob, behavior);
                changed++;
            } catch (Exception e) {
                player.sendMessage("Failed to change behavior for " + mob.getType() + ": " + e.getMessage());
            }
        }
        
        player.sendMessage("Changed behavior for " + changed + " mobs to " + behavior);
        return true;
    }
    
    private boolean handleRemove(Player player, String[] args) {
        player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10)
            .stream()
            .filter(entity -> entity instanceof Mob)
            .map(entity -> (Mob) entity)
            .forEach(mob -> {
                MobAIManager.getInstance().removeBehavior(mob);
                player.sendMessage("Removed custom behavior from " + mob.getType());
            });
        
        return true;
    }
    
    private boolean handleInfo(Player player, String[] args) {
        player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10)
            .stream()
            .filter(entity -> entity instanceof Mob)
            .map(entity -> (Mob) entity)
            .forEach(mob -> {
                var behavior = MobAIManager.getInstance().getCurrentBehavior(mob);
                if (behavior != null) {
                    player.sendMessage(mob.getType() + " has behavior: " + behavior.getId());
                } else {
                    player.sendMessage(mob.getType() + " has no custom behavior");
                }
            });
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§6=== Mob AI Commands ===");
        player.sendMessage("§e/mobai spawn <mob_type> <behavior> §7- Spawn mob with behavior");
        player.sendMessage("§e/mobai change <behavior> §7- Change nearby mobs' behavior");
        player.sendMessage("§e/mobai remove §7- Remove custom behavior from nearby mobs");
        player.sendMessage("§e/mobai info §7- Show behavior info for nearby mobs");
        player.sendMessage("§7Available behaviors: aggressive, neutral, passive");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("spawn", "change", "remove", "info");
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            return Arrays.stream(EntityType.values())
                .filter(EntityType::isAlive)
                .map(type -> type.name().toLowerCase())
                .collect(Collectors.toList());
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("spawn") || 
            args.length == 2 && args[0].equalsIgnoreCase("change")) {
            return Arrays.asList("aggressive", "neutral", "passive");
        }
        
        return null;
    }
}

// ==================== EVENT-BASED USAGE ====================
package com.yourplugin.examples;

import com.yourplugin.mobai.MobAIManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EventBasedUsage implements Listener {

    /**
     * Automatically apply behaviors to naturally spawned mobs
     */
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Mob)) return;
        
        Mob mob = (Mob) event.getEntity();
        
        // Apply different behaviors based on mob type
        switch (mob.getType()) {
            case ZOMBIE:
            case SKELETON:
                MobAIManager.getInstance().applyBehavior(mob, "aggressive");
                break;
            case COW:
            case SHEEP:
            case CHICKEN:
                MobAIManager.getInstance().applyBehavior(mob, "passive");
                break;
            case WOLF:
            case IRON_GOLEM:
                MobAIManager.getInstance().applyBehavior(mob, "neutral");
                break;
        }
    }
    
    /**
     * Let players right-click mobs to change their behavior
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Mob)) return;
        
        Player player = event.getPlayer();
        Mob mob = (Mob) event.getRightClicked();
        
        // Cycle through behaviors on right-click
        var currentBehavior = MobAIManager.getInstance().getCurrentBehavior(mob);
        String newBehavior;
        
        if (currentBehavior == null) {
            newBehavior = "neutral";
        } else {
            switch (currentBehavior.getId()) {
                case "neutral":
                    newBehavior = "aggressive";
                    break;
                case "aggressive":
                    newBehavior = "passive";
                    break;
                case "passive":
                default:
                    newBehavior = "neutral";
                    break;
            }
        }
        
        try {
            MobAIManager.getInstance().changeBehavior(mob, newBehavior);
            player.sendMessage("Changed " + mob.getType() + " behavior to " + newBehavior);
        } catch (Exception e) {
            player.sendMessage("Failed to change behavior: " + e.getMessage());
        }
    }
    
    /**
     * Make mobs more aggressive when they take damage
     */
    @EventHandler
    public void onMobDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Mob)) return;
        if (!(event.getDamager() instanceof Player)) return;
        
        Mob mob = (Mob) event.getEntity();
        var currentBehavior = MobAIManager.getInstance().getCurrentBehavior(mob);
        
        // If mob is passive or neutral, make it aggressive when attacked
        if (currentBehavior != null && 
            (currentBehavior.getId().equals("passive") || currentBehavior.getId().equals("neutral"))) {
            
            MobAIManager.getInstance().changeBehavior(mob, "aggressive");
            
            // Optional: Add visual effects
            mob.getWorld().strikeLightningEffect(mob.getLocation());
            mob.setCustomName("§cEnraged " + mob.getType().name());
            mob.setCustomNameVisible(true);
        }
    }
}

// ==================== CUSTOM BEHAVIOR EXAMPLE ====================
package com.yourplugin.examples;

import com.yourplugin.mobai.api.BehaviorType;
import com.yourplugin.mobai.api.MobBehavior;
import com.yourplugin.mobai.MobAIManager;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
* Example custom behavior: Guard behavior
* Mob stays near a specific location and attacks anything that comes close
  */
  public class GuardBehavior implements MobBehavior {

  @Override
  public void onApply(Mob mob) {
  // Set the guard position to where the mob currently is
  mob.setMetadata("guard_x", new org.bukkit.metadata.FixedMetadataValue(
  org.bukkit.Bukkit.getPluginManager().getPlugin("YourPlugin"),
  mob.getLocation().getX()
  ));
  mob.setMetadata("guard_z", new org.bukkit.metadata.FixedMetadataValue(
  org.bukkit.Bukkit.getPluginManager().getPlugin("YourPlugin"),
  mob.getLocation().getZ()
  ));

       mob.setCustomName("§6Guard");
       mob.setCustomNameVisible(true);
  }

  @Override
  public void onRemove(Mob mob) {
  mob.removeMetadata("guard_x", org.bukkit.Bukkit.getPluginManager().getPlugin("YourPlugin"));
  mob.removeMetadata("guard_z", org.bukkit.Bukkit.getPluginManager().getPlugin("YourPlugin"));
  mob.setCustomName(null);
  mob.setCustomNameVisible(false);
  }

  @Override
  public void onTick(Mob mob) {
  // Check if mob is too far from guard position
  if (mob.hasMetadata("guard_x") && mob.hasMetadata("guard_z")) {
  double guardX = mob.getMetadata("guard_x").get(0).asDouble();
  double guardZ = mob.getMetadata("guard_z").get(0).asDouble();
* 
           double distance = Math.sqrt(
               Math.pow(mob.getLocation().getX() - guardX, 2) + 
               Math.pow(mob.getLocation().getZ() - guardZ, 2)
           );
           
           // If too far from guard post, return to it
           if (distance > 10.0 && mob.getTarget() == null) {
               Location guardLocation = new Location(mob.getWorld(), guardX, mob.getLocation().getY(), guardZ);
               mob.getPathfinder().moveTo(guardLocation, 1.0);
           }
           
           // Look for nearby players to attack
           Player nearestPlayer = mob.getWorld().getNearbyPlayers(mob.getLocation(), 8.0)
               .stream()
               .filter(p -> !p.isCreative() && !p.isSpectator())
               .findFirst()
               .orElse(null);
           
           if (nearestPlayer != null && mob.getTarget() == null) {
               mob.setTarget(nearestPlayer);
           }
       }
  }

  @Override
  public BehaviorType getType() {
  return BehaviorType.CUSTOM;
  }

  @Override
  public String getId() {
  return "guard";
  }

  @Override
  public boolean isApplicable(Mob mob) {
  // Guards can be any hostile mob
  return mob.getType().name().contains("ZOMBIE") ||
  mob.getType().name().contains("SKELETON") ||
  mob.getType().name().contains("GOLEM");
  }
  }

// ==================== PLUGIN MAIN CLASS ====================
package com.yourplugin.examples;

import com.yourplugin.mobai.MobAIManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register our custom behavior
        MobAIManager.getInstance().registerBehavior(new GuardBehavior());
        
        // Register command
        getCommand("mobai").setExecutor(new MobAICommand());
        getCommand("mobai").setTabCompleter(new MobAICommand());
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(new EventBasedUsage(), this);
        
        getLogger().info("Example MobAI Plugin enabled!");
    }
    
    // Quick utility methods for other plugins to use
    public static void makeAggressive(Mob mob) {
        MobAIManager.getInstance().applyBehavior(mob, "aggressive");
    }
    
    public static void makePassive(Mob mob) {
        MobAIManager.getInstance().applyBehavior(mob, "passive");
    }
    
    public static void makeNeutral(Mob mob) {
        MobAIManager.getInstance().applyBehavior(mob, "neutral");
    }
    
    public static void makeGuard(Mob mob) {
        MobAIManager.getInstance().applyBehavior(mob, "guard");
    }
}