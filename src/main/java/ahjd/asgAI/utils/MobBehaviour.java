package ahjd.asgAI.utils;


import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public interface MobBehaviour {

    /**
     * Called when behavior is applied to a mob
     */
    void onApply(Mob mob);

    /**
     * Called when behavior is removed from a mob
     */
    void onRemove(Mob mob);

    /**
     * Called when mob takes damage
     */
    default void onDamage(Mob mob, EntityDamageByEntityEvent event) {}

    /**
     * Called when mob dies
     */
    default void onDeath(Mob mob, EntityDeathEvent event) {}

    /**
     * Called when mob targets something
     */
    default void onTarget(Mob mob, EntityTargetEvent event) {}

    /**
     * Called every tick while behavior is active
     */
    default void onTick(Mob mob) {}

    /**
     * Get the behavior type
     */
    BehaviourEnums.BehaviourType getType();

    /**
     * Get unique identifier for this behavior
     */
    String getId();

    /**
     * Check if behavior can be applied to this mob type
     */
    boolean isApplicable(Mob mob);
}