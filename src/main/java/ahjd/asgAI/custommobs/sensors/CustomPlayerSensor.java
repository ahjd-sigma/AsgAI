package ahjd.asgAI.custommobs.sensors;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.WeakHashMap;

public class CustomPlayerSensor implements CustomSensor {
    private final double detectionRange;
    private final boolean alertOnDetection;
    private static final WeakHashMap<Mob, BukkitTask> activeTasks = new WeakHashMap<>();
    
    public CustomPlayerSensor() {
        this(16.0, false);
    }
    
    public CustomPlayerSensor(double detectionRange, boolean alertOnDetection) {
        this.detectionRange = detectionRange;
        this.alertOnDetection = alertOnDetection;
    }
    
    @Override
    public void attachToMob(Mob mob) {
        // Create a repeating task to check for players
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
        
        // Find nearby players
        List<Player> nearbyPlayers = mob.level().getEntitiesOfClass(
            Player.class,
            mob.getBoundingBox().inflate(detectionRange),
            player -> !player.isSpectator() && !player.isCreative()
        );

        if (!nearbyPlayers.isEmpty()) {
            onPlayerDetected(mob, nearbyPlayers.get(0)); // Target closest player
        }
    }
    
    private void onPlayerDetected(Mob mob, Player player) {
        // Set the player as target if mob doesn't have one
        if (mob.getTarget() == null) {
            mob.setTarget(player);
        }

        if (alertOnDetection) {
            // Alert nearby mobs of the same type
            List<Mob> nearbyMobs = mob.level().getEntitiesOfClass(
                    Mob.class,
                    mob.getBoundingBox().inflate(detectionRange * 2),
                    otherMob -> otherMob != mob
                            && otherMob.getClass() == mob.getClass()
                            && otherMob.getTarget() == null
            );
            for (Mob nearbyMob : nearbyMobs) {
                nearbyMob.setTarget(player);
            }
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
        return "custom_player_sensor";
    }
    
    @Override
    public String getDescription() {
        return "Detects nearby players and can alert other mobs";
    }
    
    @Override
    public int getUpdateInterval() {
        return 10; // Update every 0.5 seconds
    }
}