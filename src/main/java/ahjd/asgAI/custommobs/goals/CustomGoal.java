package ahjd.asgAI.custommobs.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * Base interface for custom goals that can be applied to mobs
 */
public interface CustomGoal {
    
    /**
     * Creates the actual NMS Goal instance for the given mob
     * @param mob The mob to create the goal for
     * @return The NMS Goal instance, or null if this goal cannot be applied to this mob
     */
    Goal createNMSGoal(Mob mob);
    
    /**
     * Gets the name/identifier of this goal type
     * @return The goal name
     */
    String getName();
    
    /**
     * Gets a description of what this goal does
     * @return The goal description
     */
    default String getDescription() {
        return "Custom goal: " + getName();
    }
    
    /**
     * Checks if this goal can be applied to the given mob type
     * @param mob The mob to check
     * @return true if this goal can be applied, false otherwise
     */
    default boolean canApplyTo(Mob mob) {
        return true;
    }
}