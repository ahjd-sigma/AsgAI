package ahjd.asgAI.api;

import ahjd.asgAI.MobAIManager;
import ahjd.asgAI.utils.BehaviourEnums;
import ahjd.asgAI.utils.MobBehaviour;
import ahjd.asgAI.utils.NMSHelper;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.entity.Mob;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Main API class for AsgAI - Provides easy access to mob AI management functionality
 * for external plugins.
 */
public class AsgAIAPI {

    /**
     * Get the AsgAI API instance
     * @return AsgAIAPI instance
     * @throws IllegalStateException if AsgAI plugin is not loaded
     */
    public static AsgAIAPI getInstance() {
        if (!MobAIManager.isInitialized()) {
            throw new IllegalStateException("AsgAI plugin is not loaded or initialized!");
        }
        return new AsgAIAPI();
    }

    /**
     * Check if AsgAI is available and ready to use
     * @return true if available, false otherwise
     */
    public static boolean isAvailable() {
        return MobAIManager.isInitialized();
    }

    private AsgAIAPI() {
        // Private constructor - use getInstance()
    }

    /**
     * Register a custom behavior
     * @param behavior The behavior to register
     */
    public void registerBehavior(MobBehaviour behavior) {
        MobAIManager.getInstance().registerBehavior(behavior);
    }

    /**
     * Apply a behavior to a mob
     * @param mob The mob to apply behavior to
     * @param behaviorId The ID of the behavior to apply
     * @throws IllegalArgumentException if behavior doesn't exist or isn't applicable
     */
    public void applyBehavior(Mob mob, String behaviorId) {
        MobAIManager.getInstance().applyBehavior(mob, behaviorId);
    }

    /**
     * Remove behavior from a mob
     * @param mob The mob to remove behavior from
     */
    public void removeBehavior(Mob mob) {
        MobAIManager.getInstance().removeBehavior(mob);
    }

    /**
     * Change a mob's behavior
     * @param mob The mob to change behavior for
     * @param newBehaviorId The new behavior ID
     */
    public void changeBehavior(Mob mob, String newBehaviorId) {
        MobAIManager.getInstance().changeBehavior(mob, newBehaviorId);
    }

    /**
     * Get the current behavior of a mob
     * @param mob The mob to check
     * @return The current behavior or null if none
     */
    public MobBehaviour getCurrentBehavior(Mob mob) {
        return MobAIManager.getInstance().getCurrentBehavior(mob);
    }

    /**
     * Get all registered behavior IDs
     * @return Set of behavior IDs
     */
    public Set<String> getRegisteredBehaviorIds() {
        return MobAIManager.getInstance().getRegisteredBehaviorIds();
    }

    /**
     * Get a registered behavior by ID
     * @param behaviorId The behavior ID
     * @return MobBehaviour instance or null if not found
     */
    public MobBehaviour getBehavior(String behaviorId) {
        return MobAIManager.getInstance().getBehavior(behaviorId);
    }

    /**
     * Check if a behavior is registered
     * @param behaviorId The behavior ID to check
     * @return true if registered, false otherwise
     */
    public boolean isBehaviorRegistered(String behaviorId) {
        return MobAIManager.getInstance().isBehaviorRegistered(behaviorId);
    }

    /**
     * Get all active behaviors
     * @return Map of mob UUIDs to their active behaviors
     */
    public Map<UUID, MobBehaviour> getActiveBehaviors() {
        return MobAIManager.getInstance().getActiveBehaviors();
    }

    /**
     * Add a custom goal to a mob with specified priority
     * @param mob The Bukkit mob
     * @param goal The NMS goal to add
     * @param priority The priority for the goal
     */
    public void addCustomGoal(Mob mob, Goal goal, BehaviourEnums.BehaviourPriority priority) {
        NMSHelper.addCustomGoal(mob, goal, priority);
    }

    /**
     * Remove custom goals from a mob by identifier
     * @param mob The Bukkit mob
     * @param identifier The identifier to match goals against
     */
    public void removeCustomGoals(Mob mob, String identifier) {
        NMSHelper.removeCustomGoals(mob, identifier);
    }

    /**
     * Clear all goals from a mob
     * @param mob The Bukkit mob
     */
    public void clearAllGoals(Mob mob) {
        NMSHelper.clearAllGoals(mob);
    }

    /**
     * Get the NMS mob instance from a Bukkit mob
     * @param bukkitMob The Bukkit mob
     * @return The NMS mob instance
     */
    public net.minecraft.world.entity.Mob getNMSMob(Mob bukkitMob) {
        return NMSHelper.getNMSMob(bukkitMob);
    }
}