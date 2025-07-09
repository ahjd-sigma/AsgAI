package ahjd.asgAI;

import ahjd.asgAI.behaviours.AggressiveBehaviour;
import ahjd.asgAI.behaviours.PassiveBehaviour;
import ahjd.asgAI.utils.MobBehaviour;
import ahjd.asgAI.behaviours.NeutralBehaviour;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MobAIManager implements Listener {

    private static MobAIManager instance;
    private final Map<String, MobBehaviour> registeredBehaviors = new HashMap<>();
    private final Map<UUID, MobBehaviour> activeBehaviors = new ConcurrentHashMap<>();
    private final JavaPlugin plugin;

    /**
     * Get the singleton instance of MobAIManager
     * @return MobAIManager instance or null if not initialized
     */
    public static MobAIManager getInstance() {
        return instance;
    }

    /**
     * Check if the MobAIManager is initialized
     * @return true if initialized, false otherwise
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    private MobAIManager(JavaPlugin plugin) {
        this.plugin = plugin;
        registerDefaultBehaviors();
        startTickTask();
    }

    public static void initialize(JavaPlugin plugin) {
        instance = new MobAIManager(plugin);
    }

    private void registerDefaultBehaviors() {
        registerBehavior(new AggressiveBehaviour());
        registerBehavior(new NeutralBehaviour());
        registerBehavior(new PassiveBehaviour());
    }

    public void registerBehavior(MobBehaviour behavior) {
        registeredBehaviors.put(behavior.getId(), behavior);
    }

    public void applyBehavior(Mob mob, String behaviorId) {
        MobBehaviour behavior = registeredBehaviors.get(behaviorId);
        if (behavior == null) {
            throw new IllegalArgumentException("Unknown behavior: " + behaviorId);
        }

        if (!behavior.isApplicable(mob)) {
            throw new IllegalArgumentException("Behavior not applicable to this mob type");
        }

        // Remove existing behavior
        removeBehavior(mob);

        // Apply new behavior
        behavior.onApply(mob);
        activeBehaviors.put(mob.getUniqueId(), behavior);
    }

    public void removeBehavior(Mob mob) {
        MobBehaviour existing = activeBehaviors.remove(mob.getUniqueId());
        if (existing != null) {
            existing.onRemove(mob);
        }
    }

    public void changeBehavior(Mob mob, String newBehaviorId) {
        applyBehavior(mob, newBehaviorId);
    }

    public MobBehaviour getCurrentBehavior(Mob mob) {
        return activeBehaviors.get(mob.getUniqueId());
    }

    /**
     * Get all registered behavior IDs
     * @return Set of behavior IDs
     */
    public Set<String> getRegisteredBehaviorIds() {
        return new HashSet<>(registeredBehaviors.keySet());
    }

    /**
     * Get a registered behavior by ID
     * @param behaviorId The behavior ID
     * @return MobBehaviour instance or null if not found
     */
    public MobBehaviour getBehavior(String behaviorId) {
        return registeredBehaviors.get(behaviorId);
    }

    /**
     * Check if a behavior is registered
     * @param behaviorId The behavior ID to check
     * @return true if registered, false otherwise
     */
    public boolean isBehaviorRegistered(String behaviorId) {
        return registeredBehaviors.containsKey(behaviorId);
    }

    /**
     * Get all active behaviors
     * @return Map of mob UUIDs to their active behaviors
     */
    public Map<UUID, MobBehaviour> getActiveBehaviors() {
        return new HashMap<>(activeBehaviors);
    }

    private void startTickTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                activeBehaviors.entrySet().removeIf(entry -> {
                    UUID uuid = entry.getKey();
                    MobBehaviour behavior = entry.getValue();
                    Entity entity = plugin.getServer().getEntity(uuid);

                    if (entity instanceof Mob mob) {
                        if (mob.isValid() && !mob.isDead()) {
                            // Only tick if required
                            if (behavior.requiresTicking()) {
                                behavior.onTick(mob);
                            }
                            return false;
                        }
                        behavior.onRemove(mob);
                    }
                    return true; // Remove invalid entries
                });
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    // Event handlers
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Mob) {
            Mob mob = (Mob) event.getEntity();
            MobBehaviour behavior = activeBehaviors.get(mob.getUniqueId());
            if (behavior != null) {
                behavior.onDamage(mob, event);
            }
        }
    }

    // In MobAIManager
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Mob mob) {
            MobBehaviour behavior = activeBehaviors.remove(mob.getUniqueId());
            if (behavior != null) {
                behavior.onDeath(mob, event);
                behavior.onRemove(mob);
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Mob) {
            Mob mob = (Mob) event.getEntity();
            MobBehaviour behavior = activeBehaviors.get(mob.getUniqueId());
            if (behavior != null) {
                behavior.onTarget(mob, event);
            }
        }
    }
}