package ahjd.asgAI.custommobs.sensors;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.WeakHashMap;

public class CustomHostileSensor implements CustomSensor {
    private final double detectionRange;
    private final boolean targetStrongestEnemy;
    private static final WeakHashMap<Mob, BukkitTask> activeTasks = new WeakHashMap<>();
    
    public CustomHostileSensor() {
        this(12.0, false);
    }
    
    public CustomHostileSensor(double detectionRange, boolean targetStrongestEnemy) {
        this.detectionRange = detectionRange;
        this.targetStrongestEnemy = targetStrongestEnemy;
    }
    
    @Override
    public void attachToMob(Mob mob) {
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
        
        // Find nearby hostile entities
        List<Mob> nearbyHostiles = mob.level().getEntitiesOfClass(
            Mob.class,
            mob.getBoundingBox().inflate(detectionRange),
            target -> target instanceof Enemy && 
                     target != mob && 
                     target.isAlive() &&
                     !isSameType(mob, target)
        );
        
        if (!nearbyHostiles.isEmpty()) {
            Mob targetEnemy = selectTarget(nearbyHostiles);
            onHostileDetected(mob, targetEnemy);
        }
    }
    
    private boolean isSameType(Mob mob1, Mob mob2) {
        return mob1.getClass().equals(mob2.getClass());
    }
    
    private Mob selectTarget(List<Mob> hostiles) {
        if (targetStrongestEnemy) {
            // Target the enemy with highest health
            return hostiles.stream()
                .max((a, b) -> Float.compare(a.getHealth(), b.getHealth()))
                .orElse(hostiles.get(0));
        } else {
            // Target the closest enemy
            return hostiles.get(0);
        }
    }
    
    private void onHostileDetected(Mob mob, Mob hostile) {
        // Only target if mob doesn't have a current target or current target is not a player
        if (mob.getTarget() == null || !(mob.getTarget() instanceof net.minecraft.world.entity.player.Player)) {
            mob.setTarget(hostile);
        }
    }
    
    @Override
    public void detachFromMob(Mob mob) {
        BukkitTask task = activeTasks.remove(mob);
        if (task != null) {
            task.cancel();
        }
    }
    
    @Override
    public String getName() {
        return "custom_hostile_sensor";
    }
    
    @Override
    public String getDescription() {
        return "Detects nearby hostile entities and targets them";
    }
    
    @Override
    public int getUpdateInterval() {
        return 15; // Update every 0.75 seconds
    }
}