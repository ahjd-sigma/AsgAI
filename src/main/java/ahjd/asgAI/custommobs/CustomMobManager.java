package ahjd.asgAI.custommobs;

import ahjd.asgAI.AsgAI;
import ahjd.asgAI.custommobs.goals.*;
import ahjd.asgAI.custommobs.sensors.*;
import ahjd.asgAI.utils.RemoveVanillaAI;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomMobManager {
    private static CustomMobManager instance;
    private final Map<String, CustomMobTemplate> mobTemplates;
    private final Map<UUID, CustomMobInstance> activeMobs;
    private final AsgAI plugin;
    
    public CustomMobManager(AsgAI plugin) {
        this.plugin = plugin;
        this.mobTemplates = new ConcurrentHashMap<>();
        this.activeMobs = new ConcurrentHashMap<>();
        instance = this;
        
        // Register default templates
        registerDefaultTemplates();
    }
    
    public static CustomMobManager getInstance() {
        return instance;
    }
    
    private void registerDefaultTemplates() {
        // Example: Aggressive Zombie Template
        CustomMobTemplate aggressiveZombie = new CustomMobTemplate("aggressive_zombie")
            .setBaseEntity(org.bukkit.entity.EntityType.ZOMBIE)
            .setDisplayName("§cAggressive Zombie")
            .setHealth(40.0)
            .setSpeed(0.35)
            .setAttackDamage(6.0)
            .setFollowRange(32.0)
            .addGoal(1, new CustomAttackGoal())
            .addGoal(2, new CustomChaseGoal())
            .addGoal(3, new CustomWanderGoal())
            .addTargetGoal(1, new CustomNearestPlayerTargetGoal())
            .addSensor(new CustomPlayerSensor())
            .addSensor(new CustomHostileSensor());
            
        registerTemplate(aggressiveZombie);
        
        // Example: Guard Zombie Template
        CustomMobTemplate guardZombie = new CustomMobTemplate("guard_zombie")
            .setBaseEntity(org.bukkit.entity.EntityType.ZOMBIE)
            .setDisplayName("§6Guard Zombie")
            .setHealth(60.0)
            .setSpeed(0.25)
            .setAttackDamage(8.0)
            .setFollowRange(16.0)
            .addGoal(1, new CustomDefendAreaGoal())
            .addGoal(2, new CustomAttackGoal())
            .addGoal(3, new CustomPatrolGoal())
            .addTargetGoal(1, new CustomNearestHostileTargetGoal())
            .addSensor(new CustomPlayerSensor())
            .addSensor(new CustomThreatSensor());
            
        registerTemplate(guardZombie);
    }
    
    public void registerTemplate(CustomMobTemplate template) {
        mobTemplates.put(template.getId(), template);
        plugin.getLogger().info("Registered custom mob template: " + template.getId());
    }
    
    public CustomMobTemplate getTemplate(String id) {
        return mobTemplates.get(id);
    }
    
    public Set<String> getTemplateIds() {
        return new HashSet<>(mobTemplates.keySet());
    }
    
    public LivingEntity spawnCustomMob(String templateId, Location location) {
        CustomMobTemplate template = getTemplate(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Unknown mob template: " + templateId);
        }
        
        // Spawn the base entity
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, template.getBaseEntity());
        
        // Apply customizations
        applyTemplate(entity, template);
        
        // Track the custom mob
        CustomMobInstance instance = new CustomMobInstance(entity, template);
        activeMobs.put(entity.getUniqueId(), instance);
        
        return entity;
    }
    
    private void applyTemplate(LivingEntity entity, CustomMobTemplate template) {
        // Set basic attributes
        entity.setMaxHealth(template.getHealth());
        entity.setHealth(template.getHealth());
        entity.setCustomName(template.getDisplayName());
        entity.setCustomNameVisible(true);
        
        // Clear vanilla AI
        RemoveVanillaAI.clearVanillaAI(entity);
        
        // Get NMS entity
        if (entity instanceof CraftLivingEntity craftEntity) {
            Mob nmsMob = (Mob) craftEntity.getHandle();
            
            // Apply custom attributes
            applyAttributes(nmsMob, template);
            
            // Add custom goals
            addCustomGoals(nmsMob, template);
            
            // Add custom sensors
            addCustomSensors(nmsMob, template);
        }
    }
    
    private void applyAttributes(Mob nmsMob, CustomMobTemplate template) {
        // Apply movement speed
        if (template.getSpeed() != null) {
            nmsMob.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
                .setBaseValue(template.getSpeed());
        }
        
        // Apply attack damage
        if (template.getAttackDamage() != null) {
            nmsMob.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
                .setBaseValue(template.getAttackDamage());
        }
        
        // Apply follow range
        if (template.getFollowRange() != null) {
            nmsMob.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE)
                .setBaseValue(template.getFollowRange());
        }
    }
    
    private void addCustomGoals(Mob nmsMob, CustomMobTemplate template) {
        // Add regular goals
        for (Map.Entry<Integer, CustomGoal> entry : template.getGoals().entrySet()) {
            Goal nmsGoal = entry.getValue().createNMSGoal(nmsMob);
            if (nmsGoal != null) {
                nmsMob.goalSelector.addGoal(entry.getKey(), nmsGoal);
            }
        }
        
        // Add target goals
        for (Map.Entry<Integer, CustomGoal> entry : template.getTargetGoals().entrySet()) {
            Goal nmsGoal = entry.getValue().createNMSGoal(nmsMob);
            if (nmsGoal != null) {
                nmsMob.targetSelector.addGoal(entry.getKey(), nmsGoal);
            }
        }
    }
    
    private void addCustomSensors(Mob nmsMob, CustomMobTemplate template) {
        // Note: Sensor implementation would require more complex NMS integration
        // For now, we'll focus on goals which are more straightforward
        for (CustomSensor sensor : template.getSensors()) {
            sensor.attachToMob(nmsMob);
        }
    }
    
    public CustomMobInstance getCustomMob(UUID entityId) {
        return activeMobs.get(entityId);
    }
    
    public void removeCustomMob(UUID entityId) {
        activeMobs.remove(entityId);
    }
    
    public Collection<CustomMobInstance> getActiveMobs() {
        return new ArrayList<>(activeMobs.values());
    }
    
    public void cleanup() {
        // Remove dead mobs from tracking
        activeMobs.entrySet().removeIf(entry -> 
            entry.getValue().getEntity().isDead() || !entry.getValue().getEntity().isValid());
    }
}