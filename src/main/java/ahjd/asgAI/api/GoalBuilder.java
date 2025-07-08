package ahjd.asgAI.api;

import ahjd.asgAI.goals.CustomAggressiveGoal;
import ahjd.asgAI.goals.CustomNeutralGoal;
import ahjd.asgAI.goals.CustomPassiveGoal;
import ahjd.asgAI.utils.BehaviourEnums;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * Builder class for creating custom goals easily
 */
public class GoalBuilder {

    /**
     * Create an aggressive goal builder
     * @param mob The NMS mob
     * @return AggressiveGoalBuilder instance
     */
    public static AggressiveGoalBuilder aggressive(Mob mob) {
        return new AggressiveGoalBuilder(mob);
    }

    /**
     * Create a neutral goal builder
     * @param mob The NMS mob
     * @return NeutralGoalBuilder instance
     */
    public static NeutralGoalBuilder neutral(Mob mob) {
        return new NeutralGoalBuilder(mob);
    }

    /**
     * Create a passive goal builder
     * @param mob The NMS mob
     * @return PassiveGoalBuilder instance
     */
    public static PassiveGoalBuilder passive(Mob mob) {
        return new PassiveGoalBuilder(mob);
    }

    /**
     * Builder for aggressive goals
     */
    public static class AggressiveGoalBuilder {
        private final Mob mob;
        private double speed = 1.0;
        private double range = 16.0;

        private AggressiveGoalBuilder(Mob mob) {
            this.mob = mob;
        }

        /**
         * Set the movement speed
         * @param speed Movement speed multiplier
         * @return This builder
         */
        public AggressiveGoalBuilder speed(double speed) {
            this.speed = speed;
            return this;
        }

        /**
         * Set the detection range
         * @param range Detection range in blocks
         * @return This builder
         */
        public AggressiveGoalBuilder range(double range) {
            this.range = range;
            return this;
        }

        /**
         * Build the aggressive goal
         * @return CustomAggressiveGoal instance
         */
        public Goal build() {
            return new CustomAggressiveGoal(mob, speed, range);
        }
    }

    /**
     * Builder for neutral goals
     */
    public static class NeutralGoalBuilder {
        private final Mob mob;

        private NeutralGoalBuilder(Mob mob) {
            this.mob = mob;
        }

        /**
         * Build the neutral goal
         * @return CustomNeutralGoal instance
         */
        public Goal build() {
            return new CustomNeutralGoal(mob);
        }
    }

    /**
     * Builder for passive goals
     */
    public static class PassiveGoalBuilder {
        private final Mob mob;

        private PassiveGoalBuilder(Mob mob) {
            this.mob = mob;
        }

        /**
         * Build the passive goal
         * @return CustomPassiveGoal instance
         */
        public Goal build() {
            return new CustomPassiveGoal(mob);
        }
    }
}