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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MobAIManager implements Listener {

    private static MobAIManager instance;
    private final Map<String, MobBehaviour> registeredBehaviors = new HashMap<>();
    private final Map<UUID, MobBehaviour> activeBehaviors = new ConcurrentHashMap<>();
    private final JavaPlugin plugin;

    private MobAIManager(JavaPlugin plugin) {
        this.plugin = plugin;
        registerDefaultBehaviors();
        startTickTask();
    }

    public static void initialize(JavaPlugin plugin) {
        instance = new MobAIManager(plugin);
    }

    public static MobAIManager getInstance() {
        return instance;
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
    private void startTickTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                activeBehaviors.entrySet().removeIf(entry -> {
                    UUID uuid = entry.getKey();
                    MobBehaviour behavior = entry.getValue();

                    Entity entity = plugin.getServer().getEntity(uuid);
                    if (entity instanceof Mob) {
                        Mob mob = (Mob) entity;
                        if (mob.isValid() && !mob.isDead()) {
                            behavior.onTick(mob);
                            return false; // Keep
                        } else {
                            behavior.onRemove(mob);
                            return true; // Remove
                        }
                    } else {
                        // Entity is null or not a mob anymore - clean up
                        return true; // Remove
                    }
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

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Mob) {
            Mob mob = (Mob) event.getEntity();
            MobBehaviour behavior = activeBehaviors.remove(mob.getUniqueId());
            if (behavior != null) {
                behavior.onDeath(mob, event);
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