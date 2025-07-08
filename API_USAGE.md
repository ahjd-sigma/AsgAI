# AsgAI API Usage Guide

## Overview

AsgAI is a powerful and extensible Mob AI API for Minecraft Paper servers. It provides a clean, modular system for creating and managing custom mob behaviors and goals.

## Adding AsgAI as a Dependency

### Using JitPack with Gradle

Add JitPack repository to your `build.gradle`:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```

Add the dependency:

```gradle
dependencies {
    compileOnly 'com.github.yourusername:AsgAI:main-SNAPSHOT'
    // Or use a specific release tag:
    // compileOnly 'com.github.yourusername:AsgAI:v1.0.0'
}
```

### Using JitPack with Maven

Add JitPack repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency:

```xml
<dependency>
    <groupId>com.github.yourusername</groupId>
    <artifactId>AsgAI</artifactId>
    <version>main-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

## Basic API Usage

### 1. Getting the API Instance

```java
import ahjd.asgAI.api.AsgAIAPI;

// Check if AsgAI is available
if (AsgAIAPI.isAvailable()) {
    AsgAIAPI api = AsgAIAPI.getInstance();
    // Use the API...
} else {
    getLogger().warning("AsgAI plugin is not loaded!");
}
```

### 2. Applying Built-in Behaviors

```java
AsgAIAPI api = AsgAIAPI.getInstance();
Mob mob = (Mob) entity;

// Apply aggressive behavior
api.applyBehavior(mob, "aggressive");

// Apply neutral behavior
api.applyBehavior(mob, "neutral");

// Apply passive behavior
api.applyBehavior(mob, "passive");

// Remove behavior
api.removeBehavior(mob);

// Change behavior
api.changeBehavior(mob, "neutral");
```

### 3. Creating Custom Behaviors

#### Using BehaviorFactory

```java
import ahjd.asgAI.api.BehaviorFactory;
import ahjd.asgAI.utils.BehaviourEnums;

// Create a custom behavior
MobBehaviour customBehavior = BehaviorFactory.custom("my_custom_behavior", BehaviourEnums.BehaviourType.CUSTOM)
    .onApply(mob -> {
        mob.sendMessage("Custom behavior applied!");
        // Add custom logic here
    })
    .onRemove(mob -> {
        mob.sendMessage("Custom behavior removed!");
    })
    .onDamage((mob, event) -> {
        // Handle damage events
        mob.sendMessage("Ouch! I was damaged!");
    })
    .onTick(mob -> {
        // Called every tick while behavior is active
        // Add periodic behavior here
    })
    .isApplicable(mob -> {
        // Define which mobs this behavior can be applied to
        return mob instanceof Zombie;
    })
    .build();

// Register the behavior
api.registerBehavior(customBehavior);

// Apply it to a mob
api.applyBehavior(mob, "my_custom_behavior");
```

#### Implementing MobBehaviour Interface

```java
import ahjd.asgAI.utils.MobBehaviour;
import ahjd.asgAI.utils.BehaviourEnums;

public class MyCustomBehavior implements MobBehaviour {
    
    @Override
    public void onApply(Mob mob) {
        // Logic when behavior is applied
    }
    
    @Override
    public void onRemove(Mob mob) {
        // Logic when behavior is removed
    }
    
    @Override
    public void onDamage(Mob mob, EntityDamageByEntityEvent event) {
        // Handle damage events
    }
    
    @Override
    public BehaviourEnums.BehaviourType getType() {
        return BehaviourEnums.BehaviourType.CUSTOM;
    }
    
    @Override
    public String getId() {
        return "my_custom_behavior";
    }
    
    @Override
    public boolean isApplicable(Mob mob) {
        return true; // Can be applied to any mob
    }
}
```

### 4. Creating Custom Goals

#### Using GoalBuilder

```java
import ahjd.asgAI.api.GoalBuilder;
import net.minecraft.world.entity.ai.goal.Goal;

// Get NMS mob
net.minecraft.world.entity.Mob nmsMob = api.getNMSMob(bukkitMob);

// Create an aggressive goal
Goal aggressiveGoal = GoalBuilder.aggressive(nmsMob)
    .speed(1.5) // Movement speed
    .range(20.0) // Detection range
    .build();

// Add the goal to a mob
api.addCustomGoal(bukkitMob, aggressiveGoal, BehaviourEnums.BehaviourPriority.HIGH);

// Create a neutral wandering goal
Goal neutralGoal = GoalBuilder.neutral(nmsMob).build();
api.addCustomGoal(bukkitMob, neutralGoal, BehaviourEnums.BehaviourPriority.NORMAL);

// Create a passive fleeing goal
Goal passiveGoal = GoalBuilder.passive(nmsMob).build();
api.addCustomGoal(bukkitMob, passiveGoal, BehaviourEnums.BehaviourPriority.HIGH);
```

#### Creating Custom Goal Classes

```java
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;

public class MyCustomGoal extends Goal {
    private final Mob mob;
    
    public MyCustomGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }
    
    @Override
    public boolean canUse() {
        // Determine when this goal should be active
        return true;
    }
    
    @Override
    public void start() {
        // Called when goal starts
    }
    
    @Override
    public void tick() {
        // Called every tick while goal is active
    }
    
    @Override
    public void stop() {
        // Called when goal stops
    }
}
```

### 5. Managing Goals

```java
// Remove specific goals by identifier
api.removeCustomGoals(mob, "aggressive");

// Clear all goals from a mob
api.clearAllGoals(mob);
```

### 6. Querying API State

```java
// Get all registered behavior IDs
Set<String> behaviorIds = api.getRegisteredBehaviorIds();

// Check if a behavior is registered
boolean isRegistered = api.isBehaviorRegistered("my_behavior");

// Get current behavior of a mob
MobBehaviour currentBehavior = api.getCurrentBehavior(mob);

// Get all active behaviors
Map<UUID, MobBehaviour> activeBehaviors = api.getActiveBehaviors();
```

## Plugin Integration Example

```java
import ahjd.asgAI.api.AsgAIAPI;
import ahjd.asgAI.api.BehaviorFactory;
import ahjd.asgAI.utils.BehaviourEnums;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    
    private AsgAIAPI asgAI;
    
    @Override
    public void onEnable() {
        // Check if AsgAI is available
        if (!AsgAIAPI.isAvailable()) {
            getLogger().severe("AsgAI plugin is required but not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        asgAI = AsgAIAPI.getInstance();
        
        // Register custom behaviors
        registerCustomBehaviors();
        
        getLogger().info("Successfully integrated with AsgAI!");
    }
    
    private void registerCustomBehaviors() {
        // Create and register a custom behavior
        MobBehaviour guardBehavior = BehaviorFactory.custom("guard", BehaviourEnums.BehaviourType.CUSTOM)
            .onApply(mob -> {
                // Make mob stay in place and attack nearby enemies
                mob.setAI(true);
            })
            .onDamage((mob, event) -> {
                // Retaliate when damaged
                if (event.getDamager() instanceof LivingEntity) {
                    mob.setTarget((LivingEntity) event.getDamager());
                }
            })
            .isApplicable(mob -> mob instanceof Monster)
            .build();
            
        asgAI.registerBehavior(guardBehavior);
    }
}
```

## Behavior Priorities

- `HIGHEST` (0) - Critical behaviors that should override everything
- `HIGH` (1) - Important behaviors like combat
- `NORMAL` (2) - Standard behaviors like wandering
- `LOW` (3) - Background behaviors
- `LOWEST` (4) - Fallback behaviors

## Best Practices

1. **Always check if AsgAI is available** before using the API
2. **Use unique behavior IDs** to avoid conflicts with other plugins
3. **Clean up behaviors** when your plugin is disabled
4. **Test behavior applicability** to ensure behaviors work with intended mob types
5. **Use appropriate priorities** for your goals to ensure proper behavior hierarchy

## Troubleshooting

- **"AsgAI plugin is not loaded"**: Ensure AsgAI plugin is installed and enabled
- **Behavior not working**: Check if the behavior is applicable to the mob type
- **Goals not executing**: Verify goal priorities and conditions
- **Compilation errors**: Ensure you're using the correct AsgAI version and have proper dependencies