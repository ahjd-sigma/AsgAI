package ahjd.asgAI.custommobs.sensors;

import net.minecraft.world.entity.Mob;

/**
 * Base interface for custom sensors that can be applied to mobs
 * Sensors are used to detect and react to environmental changes
 */
public interface CustomSensor {
    
    /**
     * Attaches this sensor to the given mob
     * @param mob The mob to attach the sensor to
     */
    void attachToMob(Mob mob);
    
    /**
     * Updates the sensor logic - called periodically
     * @param mob The mob this sensor is attached to
     */
    void update(Mob mob);
    
    /**
     * Gets the name/identifier of this sensor type
     * @return The sensor name
     */
    String getName();
    
    /**
     * Gets a description of what this sensor does
     * @return The sensor description
     */
    default String getDescription() {
        return "Custom sensor: " + getName();
    }
    
    /**
     * Gets the update interval for this sensor in ticks
     * @return The number of ticks between updates
     */
    default int getUpdateInterval() {
        return 20; // Default to 1 second
    }
    
    /**
     * Checks if this sensor can be applied to the given mob type
     * @param mob The mob to check
     * @return true if this sensor can be applied, false otherwise
     */
    default boolean canApplyTo(Mob mob) {
        return true;
    }
    
    /**
     * Called when the sensor is removed from a mob
     * @param mob The mob the sensor is being removed from
     */
    default void detachFromMob(Mob mob) {
        // Default implementation does nothing
    }
}