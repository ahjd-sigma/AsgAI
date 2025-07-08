package ahjd.asgAI.api;

import ahjd.asgAI.behaviours.AggressiveBehaviour;
import ahjd.asgAI.behaviours.NeutralBehaviour;
import ahjd.asgAI.behaviours.PassiveBehaviour;
import ahjd.asgAI.utils.BehaviourEnums;
import ahjd.asgAI.utils.MobBehaviour;
import ahjd.asgAI.utils.NMSHelper;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Factory class for creating custom behaviors easily
 */
public class BehaviorFactory {

    /**
     * Create a new aggressive behavior instance
     * @return AggressiveBehaviour instance
     */
    public static MobBehaviour createAggressive() {
        return new AggressiveBehaviour();
    }

    /**
     * Create a new neutral behavior instance
     * @return NeutralBehaviour instance
     */
    public static MobBehaviour createNeutral() {
        return new NeutralBehaviour();
    }

    /**
     * Create a new passive behavior instance
     * @return PassiveBehaviour instance
     */
    public static MobBehaviour createPassive() {
        return new PassiveBehaviour();
    }

    /**
     * Create a custom behavior builder
     * @param id The unique identifier for this behavior
     * @param type The behavior type
     * @return CustomBehaviorBuilder instance
     */
    public static CustomBehaviorBuilder custom(String id, BehaviourEnums.BehaviourType type) {
        return new CustomBehaviorBuilder(id, type);
    }

    /**
     * Builder for creating custom behaviors
     */
    public static class CustomBehaviorBuilder {
        private final String id;
        private final BehaviourEnums.BehaviourType type;
        private Consumer<Mob> onApply;
        private Consumer<Mob> onRemove;
        private BiConsumer<Mob, EntityDamageByEntityEvent> onDamage;
        private BiConsumer<Mob, EntityDeathEvent> onDeath;
        private BiConsumer<Mob, EntityTargetEvent> onTarget;
        private Consumer<Mob> onTick;
        private Predicate<Mob> isApplicable = mob -> true;

        private CustomBehaviorBuilder(String id, BehaviourEnums.BehaviourType type) {
            this.id = id;
            this.type = type;
        }

        /**
         * Set the action to perform when behavior is applied
         * @param onApply Consumer that takes the mob
         * @return This builder
         */
        public CustomBehaviorBuilder onApply(Consumer<Mob> onApply) {
            this.onApply = onApply;
            return this;
        }

        /**
         * Set the action to perform when behavior is removed
         * @param onRemove Consumer that takes the mob
         * @return This builder
         */
        public CustomBehaviorBuilder onRemove(Consumer<Mob> onRemove) {
            this.onRemove = onRemove;
            return this;
        }

        /**
         * Set the action to perform when mob takes damage
         * @param onDamage BiConsumer that takes the mob and damage event
         * @return This builder
         */
        public CustomBehaviorBuilder onDamage(BiConsumer<Mob, EntityDamageByEntityEvent> onDamage) {
            this.onDamage = onDamage;
            return this;
        }

        /**
         * Set the action to perform when mob dies
         * @param onDeath BiConsumer that takes the mob and death event
         * @return This builder
         */
        public CustomBehaviorBuilder onDeath(BiConsumer<Mob, EntityDeathEvent> onDeath) {
            this.onDeath = onDeath;
            return this;
        }

        /**
         * Set the action to perform when mob targets something
         * @param onTarget BiConsumer that takes the mob and target event
         * @return This builder
         */
        public CustomBehaviorBuilder onTarget(BiConsumer<Mob, EntityTargetEvent> onTarget) {
            this.onTarget = onTarget;
            return this;
        }

        /**
         * Set the action to perform every tick
         * @param onTick Consumer that takes the mob
         * @return This builder
         */
        public CustomBehaviorBuilder onTick(Consumer<Mob> onTick) {
            this.onTick = onTick;
            return this;
        }

        /**
         * Set the predicate to check if behavior is applicable to a mob
         * @param isApplicable Predicate that takes the mob and returns boolean
         * @return This builder
         */
        public CustomBehaviorBuilder isApplicable(Predicate<Mob> isApplicable) {
            this.isApplicable = isApplicable;
            return this;
        }

        /**
         * Add a goal to be applied when this behavior is activated
         * @param goal The goal to add
         * @param priority The priority for the goal
         * @return This builder
         */
        public CustomBehaviorBuilder addGoal(Goal goal, BehaviourEnums.BehaviourPriority priority) {
            Consumer<Mob> currentOnApply = this.onApply;
            this.onApply = mob -> {
                if (currentOnApply != null) {
                    currentOnApply.accept(mob);
                }
                NMSHelper.addCustomGoal(mob, goal, priority);
            };
            return this;
        }

        /**
         * Build the custom behavior
         * @return MobBehaviour instance
         */
        public MobBehaviour build() {
            return new MobBehaviour() {
                @Override
                public void onApply(Mob mob) {
                    if (onApply != null) {
                        onApply.accept(mob);
                    }
                }

                @Override
                public void onRemove(Mob mob) {
                    if (onRemove != null) {
                        onRemove.accept(mob);
                    }
                }

                @Override
                public void onDamage(Mob mob, EntityDamageByEntityEvent event) {
                    if (onDamage != null) {
                        onDamage.accept(mob, event);
                    }
                }

                @Override
                public void onDeath(Mob mob, EntityDeathEvent event) {
                    if (onDeath != null) {
                        onDeath.accept(mob, event);
                    }
                }

                @Override
                public void onTarget(Mob mob, EntityTargetEvent event) {
                    if (onTarget != null) {
                        onTarget.accept(mob, event);
                    }
                }

                @Override
                public void onTick(Mob mob) {
                    if (onTick != null) {
                        onTick.accept(mob);
                    }
                }

                @Override
                public BehaviourEnums.BehaviourType getType() {
                    return type;
                }

                @Override
                public String getId() {
                    return id;
                }

                @Override
                public boolean isApplicable(Mob mob) {
                    return isApplicable.test(mob);
                }
            };
        }
    }
}