# AsgAI - Advanced Mob Goal API for Minecraft Paper

**Version:** 1.21.5  
**Platform:** Paper/Paperweight  
**API Version:** 1.21

AsgAI is a powerful and flexible goal-giving API for Minecraft mobs that allows developers to create custom behaviors without directly modifying NMS (Net Minecraft Server) code. The plugin provides a streamlined, single-file API that makes mob AI customization accessible and maintainable.

## Features

- **🎯 Three Core Behavior Types**: Passive, Neutral, and Aggressive with natural behaviors
- **🔧 Custom Goal System**: Register and apply unlimited custom goals
- **👁️ Smart Visibility Checks**: Realistic line-of-sight detection for aggressive behaviors
- **🔄 Backward Compatibility**: Maintains existing command structures
- **📦 Single API File**: Everything you need in one streamlined interface
- **🚀 Easy Integration**: Simple dependency setup via JitPack

## Quick Start

### Adding AsgAI as a Dependency

#### Gradle
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.ahjd-sigma:AsgAI:main-SNAPSHOT'
}
```

#### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.ahjd-sigma</groupId>
        <artifactId>AsgAI</artifactId>
        <version>main-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Basic Usage

```java
import ahjd.asgAI.api.AsgAIAPI;
import org.bukkit.entity.Mob;

public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Check if AsgAI is available
        if (!AsgAIAPI.isAvailable()) {
            getLogger().warning("AsgAI is not available!");
            return;
        }
        
        AsgAIAPI api = AsgAIAPI.getInstance();
        
        // Apply behaviors to mobs
        Mob zombie = // ... get your mob
        
        // Make zombie aggressive (will attack players on sight)
        api.applyAggressiveBehavior(zombie, 16.0, 1.5); // 16 block range, 1.5x speed
        
        // Make sheep passive (will flee from players)
        api.applyPassiveBehavior(sheep, true); // true = include flee behavior
        
        // Make cow neutral (just wanders around)
        api.applyNeutralBehavior(cow);
    }
}
```

## Core Behavior Types

### 1. Passive Behavior
Perfect for peaceful mobs like sheep, chickens, and villagers.

```java
AsgAIAPI api = AsgAIAPI.getInstance();

// Basic passive behavior (wander, idle, look at players)
api.applyPassiveBehavior(mob, false);

// Passive with flee behavior (runs away from players)
api.applyPassiveBehavior(mob, true);
```

**Included Goals:**
- 🚶 **Wander**: Random movement around the area
- 👀 **Look Around**: Occasional random looking
- 👁️ **Look at Player**: Watches nearby players
- 🏃 **Flee** (optional): Runs away from players within 12 blocks

### 2. Neutral Behavior
Ideal for mobs that should be mostly inactive but still show life.

```java
// Neutral mobs just wander and look around occasionally
api.applyNeutralBehavior(mob);
```

**Included Goals:**
- 🚶 **Wander**: Slow random movement
- 👀 **Look Around**: Random looking behavior
- 😴 **Idle**: Occasional standing still

### 3. Aggressive Behavior
For hostile mobs that should attack players on sight.

```java
// Aggressive behavior with custom range and speed
api.applyAggressiveBehavior(mob, 20.0, 2.0); // 20 block range, 2x speed
```

**Included Goals:**
- ⚔️ **Attack Player**: Targets and attacks visible players
- 🚶 **Wander**: Patrols when no targets
- 👀 **Look Around**: Scans for threats
- 😴 **Idle**: Occasional rest periods

**Smart Features:**
- ✅ Line-of-sight checks (won't attack through walls)
- ✅ Ignores creative/spectator players
- ✅ Natural pursuit behavior with grace periods
- ✅ Attack cooldowns to prevent spam

## Custom Goal System

### Creating Custom Goals

```java
import ahjd.asgAI.api.AsgAIAPI;
import ahjd.asgAI.utils.BehaviourEnums;
import net.minecraft.world.entity.ai.goal.Goal;

public class MyCustomGoals {
    
    public void registerCustomGoals() {
        AsgAIAPI api = AsgAIAPI.getInstance();
        
        // Register a custom "dance" goal
        api.registerCustomGoal("dance", mob -> new DanceGoal(mob));
        
        // Register a "follow_player" goal
        api.registerCustomGoal("follow_player", mob -> 
            new FollowPlayerGoal(mob, 2.0, 10.0)); // speed, range
    }
    
    public void applyCustomGoal(Mob mob) {
        AsgAIAPI api = AsgAIAPI.getInstance();
        
        // Apply the custom dance goal with high priority
        api.applyCustomGoal(mob, "dance", BehaviourEnums.BehaviourPriority.HIGH);
    }
}
```

### Example Custom Goal Implementation

```java
import net.minecraft.world.entity.ai.goal.Goal;
import ahjd.asgAI.utils.IdentifiableGoal;

public class DanceGoal extends Goal implements IdentifiableGoal {
    private final net.minecraft.world.entity.Mob mob;
    private int danceTime;
    private int danceStep;
    
    public DanceGoal(net.minecraft.world.entity.Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }
    
    @Override
    public String getGoalId() {
        return "dance";
    }
    
    @Override
    public boolean canUse() {
        // Dance randomly every ~10 seconds
        return mob.getRandom().nextInt(200) == 0;
    }
    
    @Override
    public void start() {
        danceTime = 60; // 3 seconds of dancing
        danceStep = 0;
        mob.getNavigation().stop();
    }
    
    @Override
    public boolean canContinueToUse() {
        return danceTime > 0;
    }
    
    @Override
    public void tick() {
        danceTime--;
        danceStep++;
        
        // Simple dance: spin around
        if (danceStep % 10 == 0) {
            float newYaw = mob.getYRot() + 45.0f;
            mob.setYRot(newYaw);
            mob.yRotO = newYaw;
        }
    }
    
    @Override
    public void stop() {
        danceTime = 0;
        danceStep = 0;
    }
}
```

## Advanced Usage

### Goal Management

```java
AsgAIAPI api = AsgAIAPI.getInstance();

// Add individual goals with specific priorities
api.addGoal(mob, new MyCustomGoal(nmsMob), BehaviourEnums.BehaviourPriority.HIGH);

// Remove specific goals by identifier
api.removeGoals(mob, "my_custom_goal");

// Clear all goals from a mob
api.clearAllGoals(mob);

// Get NMS mob instance for advanced operations
net.minecraft.world.entity.Mob nmsMob = api.getNMSMob(bukkitMob);
```

### Working with Priorities

```java
// Available priorities (highest to lowest)
BehaviourEnums.BehaviourPriority.HIGHEST  // 0 - Most important
BehaviourEnums.BehaviourPriority.HIGH     // 1
BehaviourEnums.BehaviourPriority.NORMAL   // 2 - Default
BehaviourEnums.BehaviourPriority.LOW      // 3
BehaviourEnums.BehaviourPriority.LOWEST   // 4 - Least important
```

### Backward Compatibility

AsgAI maintains compatibility with the old behavior system:

```java
// Old behavior system still works
api.registerBehavior(myOldBehavior);
api.applyBehavior(mob, "old_behavior_id");
```

## Complete Plugin Example

```java
package com.example.myplugin;

import ahjd.asgAI.api.AsgAIAPI;
import ahjd.asgAI.utils.BehaviourEnums;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MyMobPlugin extends JavaPlugin {
    
    private AsgAIAPI asgAI;
    
    @Override
    public void onEnable() {
        // Check if AsgAI is available
        if (!AsgAIAPI.isAvailable()) {
            getLogger().severe("AsgAI is required but not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        asgAI = AsgAIAPI.getInstance();
        registerCustomGoals();
        
        getLogger().info("MyMobPlugin enabled with AsgAI integration!");
    }
    
    private void registerCustomGoals() {
        // Register a custom "guard" goal
        asgAI.registerCustomGoal("guard", mob -> new GuardLocationGoal(mob, mob.blockPosition()));
        
        // Register a "party" goal that makes mobs jump
        asgAI.registerCustomGoal("party", mob -> new PartyGoal(mob));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("mobai")) {
            if (args.length < 1) {
                player.sendMessage("Usage: /mobai <passive|neutral|aggressive|guard|party>");
                return true;
            }
            
            // Get the mob the player is looking at
            Mob targetMob = getTargetMob(player);
            if (targetMob == null) {
                player.sendMessage("Look at a mob to change its behavior!");
                return true;
            }
            
            String behavior = args[0].toLowerCase();
            switch (behavior) {
                case "passive":
                    asgAI.applyPassiveBehavior(targetMob, true);
                    player.sendMessage("Applied passive behavior (with flee)!");
                    break;
                    
                case "neutral":
                    asgAI.applyNeutralBehavior(targetMob);
                    player.sendMessage("Applied neutral behavior!");
                    break;
                    
                case "aggressive":
                    asgAI.applyAggressiveBehavior(targetMob, 16.0, 1.8);
                    player.sendMessage("Applied aggressive behavior!");
                    break;
                    
                case "guard":
                    asgAI.clearAllGoals(targetMob);
                    asgAI.applyCustomGoal(targetMob, "guard", BehaviourEnums.BehaviourPriority.HIGHEST);
                    player.sendMessage("Mob is now guarding this location!");
                    break;
                    
                case "party":
                    asgAI.applyCustomGoal(targetMob, "party", BehaviourEnums.BehaviourPriority.HIGH);
                    player.sendMessage("Let's party! 🎉");
                    break;
                    
                default:
                    player.sendMessage("Unknown behavior: " + behavior);
                    break;
            }
            
            return true;
        }
        
        return false;
    }
    
    private Mob getTargetMob(Player player) {
        // Simple raycast to find the mob the player is looking at
        return player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getEyeLocation().getDirection(),
            10.0
        ).getHitEntity() instanceof Mob mob ? mob : null;
    }
}
```

## Best Practices

### 1. Goal Design
- ✅ Always implement `IdentifiableGoal` for custom goals
- ✅ Use appropriate goal flags (`Goal.Flag.MOVE`, `Goal.Flag.LOOK`, etc.)
- ✅ Include natural randomness to avoid robotic behavior
- ✅ Respect Minecraft's tick system (20 ticks = 1 second)

### 2. Performance
- ✅ Use efficient distance checks (`distanceToSqr` instead of `distanceTo`)
- ✅ Implement proper cooldowns for expensive operations
- ✅ Cache frequently accessed values
- ✅ Avoid creating new objects in `tick()` methods

### 3. Compatibility
- ✅ Always check `AsgAIAPI.isAvailable()` before using the API
- ✅ Handle cases where AsgAI might be disabled
- ✅ Use the backward compatibility methods when needed
- ✅ Test with different mob types

## Troubleshooting

### Common Issues

**"AsgAI plugin is not loaded or initialized!"**
- Ensure AsgAI plugin is installed and enabled
- Check that your plugin loads after AsgAI (use `depend` or `softdepend` in plugin.yml)

**Mobs not responding to goals**
- Verify the mob type supports AI goals
- Check that goals aren't conflicting with each other
- Ensure proper priority settings

**Performance issues**
- Review custom goal implementations for efficiency
- Reduce the frequency of expensive operations
- Use appropriate goal priorities

### Debug Information

```java
// Get information about registered goals
Set<String> customGoals = api.getCustomGoalTemplates();
getLogger().info("Registered custom goals: " + customGoals);

// Check if specific goals are registered
if (api.getCustomGoalTemplates().contains("my_goal")) {
    getLogger().info("My custom goal is registered!");
}
```

## API Reference

### Core Methods

| Method | Description |
|--------|-------------|
| `getInstance()` | Get the AsgAI API instance |
| `isAvailable()` | Check if AsgAI is ready to use |
| `applyPassiveBehavior(mob, flee)` | Apply passive behavior |
| `applyNeutralBehavior(mob)` | Apply neutral behavior |
| `applyAggressiveBehavior(mob, range, speed)` | Apply aggressive behavior |

### Custom Goal Methods

| Method | Description |
|--------|-------------|
| `registerCustomGoal(id, template)` | Register a custom goal template |
| `applyCustomGoal(mob, id, priority)` | Apply a custom goal to a mob |
| `getCustomGoalTemplates()` | Get all registered custom goal IDs |

### Goal Management

| Method | Description |
|--------|-------------|
| `addGoal(mob, goal, priority)` | Add a goal with specific priority |
| `removeGoals(mob, identifier)` | Remove goals by identifier |
| `clearAllGoals(mob)` | Remove all goals from a mob |
| `getNMSMob(bukkitMob)` | Get NMS mob instance |

## Contributing

We welcome contributions! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, please:
- Open an issue on GitHub
- Join our Discord server
- Check the wiki for additional documentation

---

**Happy coding! 🚀**