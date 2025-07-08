# Example Plugin Using AsgAI API

This is a complete example of how to create a plugin that uses AsgAI as a dependency.

## Project Structure

```
MyPlugin/
├── build.gradle
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── myplugin/
│       │               ├── MyPlugin.java
│       │               └── CustomBehaviors.java
│       └── resources/
│           └── plugin.yml
```

## build.gradle

```gradle
plugins {
    id 'java'
    id 'com.gradleup.shadow' version '8.3.0'
}

group = 'com.example'
version = '1.0.0'

repositories {
    mavenCentral()
    maven { url = 'https://jitpack.io' }
    maven {
        name = 'papermc-repo'
        url = 'https://repo.papermc.io/repository/maven-public/'
    }
}

dependencies {
    compileOnly 'io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT'
    compileOnly 'com.github.yourusername:AsgAI:main-SNAPSHOT'
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

shadowJar {
    archiveClassifier.set('')
}

build {
    dependsOn shadowJar
}
```

## plugin.yml

```yaml
name: MyPlugin
version: '1.0.0'
main: com.example.myplugin.MyPlugin
api-version: '1.21'
author: YourName
description: 'Example plugin using AsgAI API'
depend: [AsgAI]
commands:
  custombehavior:
    description: 'Apply custom behavior to nearby mobs'
    usage: '/custombehavior <behavior>'
    permission: myplugin.use
permissions:
  myplugin.use:
    description: 'Allows use of custom behavior commands'
    default: op
```

## MyPlugin.java

```java
package com.example.myplugin;

import ahjd.asgAI.api.AsgAIAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    
    private AsgAIAPI asgAI;
    private CustomBehaviors customBehaviors;
    
    @Override
    public void onEnable() {
        // Check if AsgAI is available
        if (!AsgAIAPI.isAvailable()) {
            getLogger().severe("AsgAI plugin is required but not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        asgAI = AsgAIAPI.getInstance();
        customBehaviors = new CustomBehaviors(asgAI);
        
        // Register custom behaviors
        customBehaviors.registerAll();
        
        getLogger().info("MyPlugin enabled successfully with AsgAI integration!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("MyPlugin disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (command.getName().equalsIgnoreCase("custombehavior")) {
            if (args.length != 1) {
                player.sendMessage("Usage: /custombehavior <behavior>");
                player.sendMessage("Available behaviors: guard, berserker, coward");
                return true;
            }
            
            return customBehaviors.applyBehaviorToNearbyMobs(player, args[0]);
        }
        
        return false;
    }
}
```

## CustomBehaviors.java

```java
package com.example.myplugin;

import ahjd.asgAI.api.AsgAIAPI;
import ahjd.asgAI.api.BehaviorFactory;
import ahjd.asgAI.api.GoalBuilder;
import ahjd.asgAI.utils.BehaviourEnums;
import ahjd.asgAI.utils.MobBehaviour;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CustomBehaviors {
    
    private final AsgAIAPI asgAI;
    
    public CustomBehaviors(AsgAIAPI asgAI) {
        this.asgAI = asgAI;
    }
    
    public void registerAll() {
        registerGuardBehavior();
        registerBerserkerBehavior();
        registerCowardBehavior();
    }
    
    private void registerGuardBehavior() {
        MobBehaviour guardBehavior = BehaviorFactory.custom("guard", BehaviourEnums.BehaviourType.CUSTOM)
            .onApply(mob -> {
                // Make mob stationary and aggressive to nearby enemies
                mob.setAI(true);
                
                // Add custom aggressive goal with higher range
                net.minecraft.world.entity.Mob nmsMob = asgAI.getNMSMob(mob);
                Goal guardGoal = GoalBuilder.aggressive(nmsMob)
                    .speed(2.0)
                    .range(25.0)
                    .build();
                    
                asgAI.addCustomGoal(mob, guardGoal, BehaviourEnums.BehaviourPriority.HIGH);
                
                // Add strength effect
                mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));
            })
            .onRemove(mob -> {
                // Remove effects when behavior is removed
                mob.removePotionEffect(PotionEffectType.STRENGTH);
                asgAI.removeCustomGoals(mob, "aggressive");
            })
            .onDamage((mob, event) -> {
                // Retaliate immediately when damaged
                if (event.getDamager() instanceof LivingEntity) {
                    mob.setTarget((LivingEntity) event.getDamager());
                }
            })
            .isApplicable(mob -> mob instanceof Monster)
            .build();
            
        asgAI.registerBehavior(guardBehavior);
    }
    
    private void registerBerserkerBehavior() {
        MobBehaviour berserkerBehavior = BehaviorFactory.custom("berserker", BehaviourEnums.BehaviourType.AGGRESSIVE)
            .onApply(mob -> {
                // Make mob extremely aggressive and fast
                net.minecraft.world.entity.Mob nmsMob = asgAI.getNMSMob(mob);
                Goal berserkerGoal = GoalBuilder.aggressive(nmsMob)
                    .speed(3.0)
                    .range(30.0)
                    .build();
                    
                asgAI.addCustomGoal(mob, berserkerGoal, BehaviourEnums.BehaviourPriority.HIGHEST);
                
                // Add multiple effects
                mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 2));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1));
            })
            .onRemove(mob -> {
                // Clean up effects
                mob.removePotionEffect(PotionEffectType.STRENGTH);
                mob.removePotionEffect(PotionEffectType.SPEED);
                mob.removePotionEffect(PotionEffectType.RESISTANCE);
                asgAI.removeCustomGoals(mob, "aggressive");
            })
            .onTick(mob -> {
                // Heal over time during berserker mode
                if (mob.getHealth() < mob.getMaxHealth()) {
                    mob.setHealth(Math.min(mob.getMaxHealth(), mob.getHealth() + 0.5));
                }
            })
            .isApplicable(mob -> mob instanceof Monster)
            .build();
            
        asgAI.registerBehavior(berserkerBehavior);
    }
    
    private void registerCowardBehavior() {
        MobBehaviour cowardBehavior = BehaviorFactory.custom("coward", BehaviourEnums.BehaviourType.PASSIVE)
            .onApply(mob -> {
                // Make mob flee from everything
                net.minecraft.world.entity.Mob nmsMob = asgAI.getNMSMob(mob);
                Goal fleeGoal = GoalBuilder.passive(nmsMob).build();
                
                asgAI.clearAllGoals(mob); // Remove all existing goals
                asgAI.addCustomGoal(mob, fleeGoal, BehaviourEnums.BehaviourPriority.HIGHEST);
                
                // Add speed to help with fleeing
                mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                mob.setTarget(null);
            })
            .onRemove(mob -> {
                mob.removePotionEffect(PotionEffectType.SPEED);
                asgAI.removeCustomGoals(mob, "passive");
            })
            .onDamage((mob, event) -> {
                // Flee even faster when damaged
                mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 4));
            })
            .isApplicable(mob -> true) // Can be applied to any mob
            .build();
            
        asgAI.registerBehavior(cowardBehavior);
    }
    
    public boolean applyBehaviorToNearbyMobs(Player player, String behaviorId) {
        if (!asgAI.isBehaviorRegistered(behaviorId)) {
            player.sendMessage("Unknown behavior: " + behaviorId);
            return false;
        }
        
        List<org.bukkit.entity.Entity> nearbyEntities = player.getNearbyEntities(10, 10, 10);
        int count = 0;
        
        for (org.bukkit.entity.Entity entity : nearbyEntities) {
            if (entity instanceof Mob) {
                Mob mob = (Mob) entity;
                try {
                    asgAI.applyBehavior(mob, behaviorId);
                    count++;
                } catch (IllegalArgumentException e) {
                    // Behavior not applicable to this mob type
                    continue;
                }
            }
        }
        
        if (count > 0) {
            player.sendMessage("Applied " + behaviorId + " behavior to " + count + " mobs!");
        } else {
            player.sendMessage("No applicable mobs found nearby.");
        }
        
        return true;
    }
}
```

## How to Use

1. **Setup**: Create the project structure and add the files above
2. **Build**: Run `./gradlew build` to compile your plugin
3. **Install**: 
   - Place the AsgAI plugin in your server's plugins folder
   - Place your compiled plugin in the plugins folder
4. **Test**: 
   - Start your server
   - Use `/custombehavior guard` to make nearby mobs into guards
   - Use `/custombehavior berserker` to make mobs go berserk
   - Use `/custombehavior coward` to make mobs flee from everything

## Key Features Demonstrated

- **Dependency Management**: Proper setup with JitPack
- **API Integration**: Safe initialization and usage
- **Custom Behaviors**: Creating complex behaviors with multiple effects
- **Goal Management**: Using the GoalBuilder for easy goal creation
- **Error Handling**: Proper exception handling and validation
- **Player Commands**: Interactive commands for testing behaviors

This example shows how easy it is to create powerful mob AI behaviors using the AsgAI API!