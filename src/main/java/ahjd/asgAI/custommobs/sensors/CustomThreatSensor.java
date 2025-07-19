package ahjd.asgAI.custommobs.sensors;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CustomThreatSensor implements CustomSensor {
    private final double detectionRange;
    private final int maxThreatLevel;
    private static final WeakHashMap<Mob, BukkitTask> activeTasks = new WeakHashMap<>();
    private static final WeakHashMap<Mob, Map<LivingEntity, Integer>> threatLevels = new WeakHashMap<>();
    
    public CustomThreatSensor() {
        this(20.0, 100);
    }
    
    public CustomThreatSensor(double detectionRange, int maxThreatLevel) {
        this.detectionRange = detectionRange;
        this.maxThreatLevel = maxThreatLevel;
    }
    
    @Override
    public void attachToMob(Mob mob) {
        threatLevels.put(mob, new HashMap<>());
        
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(
            ahjd.asgAI.AsgAI.getInstance(),
            () -> update(mob),
            0L,
            getUpdateInterval()
        );
        activeTasks.put(mob, task);
    }
    
    @Override
    public void update(Mob mob) {
        if (mob == null || !mob.isAlive()) {
            return;
        }
        
        Map<LivingEntity, Integer> mobThreatLevels = threatLevels.get(mob);
        if (mobThreatLevels == null) {
            return;
        }
        
        // Find nearby entities
        List<LivingEntity> nearbyEntities = mob.level().getEntitiesOfClass(
            LivingEntity.class,
            mob.getBoundingBox().inflate(detectionRange),
            entity -> entity != mob && entity.isAlive()
        );
        
        // Update threat levels
        for (LivingEntity entity : nearbyEntities) {
            int currentThreat = mobThreatLevels.getOrDefault(entity, 0);
            int newThreat = calculateThreatLevel(mob, entity);
            
            mobThreatLevels.put(entity, Math.min(maxThreatLevel, Math.max(0, currentThreat + newThreat)));
        }
        
        // Decay threat levels for entities not nearby
        mobThreatLevels.entrySet().removeIf(entry -> {
            if (!nearbyEntities.contains(entry.getKey())) {
                entry.setValue(Math.max(0, entry.getValue() - 5)); // Decay by 5 per update
                return entry.getValue() <= 0;
            }
            return false;
        });
        
        // Select highest threat target
        LivingEntity highestThreat = mobThreatLevels.entrySet().stream()
            .filter(entry -> entry.getValue() > 20) // Minimum threat threshold
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        if (highestThreat != null && (mob.getTarget() == null || 
            mobThreatLevels.getOrDefault(mob.getTarget(), 0) < mobThreatLevels.get(highestThreat))) {
            mob.setTarget(highestThreat);
        }
    }
    
    private int calculateThreatLevel(Mob mob, LivingEntity entity) {
        int threat = 0;
        
        // Base threat based on entity type
        if (entity instanceof Player player) {
            threat += 15;
            
            // Higher threat for players with weapons
            if (!player.getMainHandItem().isEmpty()) {
                threat += 10;
            }
            
            // Higher threat for players in creative/spectator
            if (player.isCreative() || player.isSpectator()) {
                threat -= 20; // Lower threat for non-threatening players
            }
        } else if (entity instanceof Mob otherMob) {
            // Threat based on other mob's attack damage
            if (otherMob.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) != null) {
                double attackDamage = otherMob.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                threat += (int) (attackDamage * 2);
            }
        }
        
        // Distance factor - closer entities are more threatening
        double distance = mob.distanceTo(entity);
        if (distance < 5.0) {
            threat += 10;
        } else if (distance < 10.0) {
            threat += 5;
        }
        
        // Health factor - injured entities are less threatening
        float healthPercent = entity.getHealth() / entity.getMaxHealth();
        if (healthPercent < 0.5f) {
            threat -= 5;
        }
        
        // If entity is targeting this mob, increase threat
        if (entity instanceof Mob otherMob && otherMob.getTarget() == mob) {
            threat += 20;
        }
        
        return threat;
    }
    
    public int getThreatLevel(Mob mob, LivingEntity entity) {
        Map<LivingEntity, Integer> mobThreatLevels = threatLevels.get(mob);
        return mobThreatLevels != null ? mobThreatLevels.getOrDefault(entity, 0) : 0;
    }
    
    @Override
    public void detachFromMob(Mob mob) {
        BukkitTask task = activeTasks.remove(mob);
        if (task != null) {
            task.cancel();
        }
        threatLevels.remove(mob);
    }
    
    @Override
    public String getName() {
        return "custom_threat_sensor";
    }
    
    @Override
    public String getDescription() {
        return "Advanced threat assessment system that tracks and prioritizes targets";
    }
    
    @Override
    public int getUpdateInterval() {
        return 20; // Update every 1 second
    }
}