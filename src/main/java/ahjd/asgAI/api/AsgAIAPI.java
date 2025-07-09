package ahjd.asgAI.api;

import ahjd.asgAI.MobAIManager;
import ahjd.asgAI.utils.BehaviourEnums;
import ahjd.asgAI.utils.MobBehaviour;
import ahjd.asgAI.utils.NMSHelper;
import ahjd.asgAI.utils.IdentifiableGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.entity.Mob;

import java.util.*;

/**
 * Streamlined AsgAI API - Complete goal-giving system for Minecraft mobs
 * Provides easy access to create and manage custom mob behaviors and goals
 */
public class AsgAIAPI {

    private static AsgAIAPI instance;
    private final Map<String, GoalTemplate> customGoalTemplates = new HashMap<>();

    /**
     * Get the AsgAI API instance
     * @return AsgAIAPI instance
     * @throws IllegalStateException if AsgAI plugin is not loaded
     */
    public static AsgAIAPI getInstance() {
        if (!MobAIManager.isInitialized()) {
            throw new IllegalStateException("AsgAI plugin is not loaded or initialized!");
        }
        if (instance == null) {
            instance = new AsgAIAPI();
        }
        return instance;
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
        initializeDefaultGoals();
    }

    // ==================== CORE GOAL TYPES ====================

    /**
     * Apply passive behavior to a mob (wander, idle, look at player, flee)
     * @param mob The mob to apply behavior to
     * @param includeFleeGoal Whether to include flee behavior when players are near
     */
    public void applyPassiveBehavior(Mob mob, boolean includeFleeGoal) {
        clearAllGoals(mob);
        net.minecraft.world.entity.Mob nmsMob = getNMSMob(mob);
        
        // Core passive goals
        addGoal(mob, new WanderGoal(nmsMob, 0.8), BehaviourEnums.BehaviourPriority.NORMAL);
        addGoal(mob, new RandomLookAroundGoal(nmsMob), BehaviourEnums.BehaviourPriority.LOW);
        addGoal(mob, new LookAtPlayerGoal(nmsMob, Player.class, 8.0f), BehaviourEnums.BehaviourPriority.NORMAL);
        
        if (includeFleeGoal) {
            addGoal(mob, new FleeFromPlayerGoal(nmsMob, 12.0, 2.2), BehaviourEnums.BehaviourPriority.HIGH);
        }
    }

    /**
     * Apply neutral behavior to a mob (wander, idle)
     * @param mob The mob to apply behavior to
     */
    public void applyNeutralBehavior(Mob mob) {
        clearAllGoals(mob);
        net.minecraft.world.entity.Mob nmsMob = getNMSMob(mob);
        
        // Neutral goals - basic wandering and looking
        addGoal(mob, new WanderGoal(nmsMob, 0.8), BehaviourEnums.BehaviourPriority.NORMAL);
        addGoal(mob, new RandomLookAroundGoal(nmsMob), BehaviourEnums.BehaviourPriority.LOW);
        addGoal(mob, new IdleGoal(nmsMob), BehaviourEnums.BehaviourPriority.LOWEST);
    }

    /**
     * Apply aggressive behavior to a mob (wander, idle, attack when player visible)
     * @param mob The mob to apply behavior to
     * @param attackRange Range at which mob will detect and attack players
     * @param attackSpeed Movement speed when pursuing targets
     */
    public void applyAggressiveBehavior(Mob mob, double attackRange, double attackSpeed) {
        clearAllGoals(mob);
        net.minecraft.world.entity.Mob nmsMob = getNMSMob(mob);
        
        // Aggressive goals
        addGoal(mob, new AttackPlayerGoal(nmsMob, attackSpeed, attackRange), BehaviourEnums.BehaviourPriority.HIGHEST);
        addGoal(mob, new WanderGoal(nmsMob, 0.8), BehaviourEnums.BehaviourPriority.NORMAL);
        addGoal(mob, new RandomLookAroundGoal(nmsMob), BehaviourEnums.BehaviourPriority.LOW);
        addGoal(mob, new IdleGoal(nmsMob), BehaviourEnums.BehaviourPriority.LOWEST);
    }

    // ==================== CUSTOM GOAL SYSTEM ====================

    /**
     * Register a custom goal template that can be applied to mobs
     * @param templateId Unique identifier for the goal template
     * @param template The goal template
     */
    public void registerCustomGoal(String templateId, GoalTemplate template) {
        customGoalTemplates.put(templateId, template);
    }

    /**
     * Apply a custom goal to a mob
     * @param mob The mob to apply the goal to
     * @param templateId The custom goal template ID
     * @param priority The priority for the goal
     */
    public void applyCustomGoal(Mob mob, String templateId, BehaviourEnums.BehaviourPriority priority) {
        GoalTemplate template = customGoalTemplates.get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Custom goal template not found: " + templateId);
        }
        
        Goal goal = template.createGoal(getNMSMob(mob));
        addGoal(mob, goal, priority);
    }

    /**
     * Get all registered custom goal template IDs
     * @return Set of custom goal template IDs
     */
    public Set<String> getCustomGoalTemplates() {
        return new HashSet<>(customGoalTemplates.keySet());
    }

    // ==================== GOAL MANAGEMENT ====================

    /**
     * Add a goal to a mob with specified priority
     * @param mob The Bukkit mob
     * @param goal The NMS goal to add
     * @param priority The priority for the goal
     */
    public void addGoal(Mob mob, Goal goal, BehaviourEnums.BehaviourPriority priority) {
        NMSHelper.addCustomGoal(mob, goal, priority);
    }

    /**
     * Remove goals from a mob by identifier
     * @param mob The Bukkit mob
     * @param identifier The identifier to match goals against
     */
    public void removeGoals(Mob mob, String identifier) {
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

    // ==================== BEHAVIOR COMPATIBILITY ====================

    /**
     * Register a custom behavior (for backward compatibility)
     * @param behavior The behavior to register
     */
    public void registerBehavior(MobBehaviour behavior) {
        MobAIManager.getInstance().registerBehavior(behavior);
    }

    /**
     * Apply a behavior to a mob (for backward compatibility)
     * @param mob The mob to apply behavior to
     * @param behaviorId The ID of the behavior to apply
     */
    public void applyBehavior(Mob mob, String behaviorId) {
        MobAIManager.getInstance().applyBehavior(mob, behaviorId);
    }

    // ==================== INITIALIZATION ====================

    private void initializeDefaultGoals() {
        // Register default custom goal templates
        registerCustomGoal("wander", mob -> new WanderGoal(mob, 1.0));
        registerCustomGoal("look_around", mob -> new RandomLookAroundGoal(mob));
        registerCustomGoal("look_at_player", mob -> new LookAtPlayerGoal(mob, Player.class, 8.0f));
    }

    // ==================== CUSTOM GOAL CLASSES ====================

    /**
     * Functional interface for creating custom goals
     */
    @FunctionalInterface
    public interface GoalTemplate {
        Goal createGoal(net.minecraft.world.entity.Mob mob);
    }

    /**
     * Wander goal - makes mobs move around randomly
     */
    public static class WanderGoal extends Goal implements IdentifiableGoal {
        private final net.minecraft.world.entity.Mob mob;
        private final double speed;
        private final double wanderRange;
        private int wanderCooldown;
        private double targetX, targetY, targetZ;

        public WanderGoal(net.minecraft.world.entity.Mob mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            this.wanderRange = 10.0;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public String getGoalId() {
            return "wander";
        }

        @Override
        public boolean canUse() {
            if (wanderCooldown > 0) {
                wanderCooldown--;
                return false;
            }

            // Only wander if not already moving and randomly
            return mob.getNavigation().isDone() && mob.getRandom().nextInt(120) == 0;
        }

        @Override
        public void start() {
            // Find a random nearby location to wander to
            double angle = mob.getRandom().nextDouble() * 2 * Math.PI;
            double distance = mob.getRandom().nextDouble() * wanderRange;

            targetX = mob.getX() + Math.cos(angle) * distance;
            targetY = mob.getY();
            targetZ = mob.getZ() + Math.sin(angle) * distance;

            mob.getNavigation().moveTo(targetX, targetY, targetZ, speed);
            wanderCooldown = 60 + mob.getRandom().nextInt(120); // 3-9 seconds
        }

        @Override
        public boolean canContinueToUse() {
            return !mob.getNavigation().isDone();
        }

        @Override
        public void stop() {
            mob.getNavigation().stop();
        }
    }

    /**
     * Idle goal - makes mobs stand still occasionally
     */
    public static class IdleGoal extends Goal implements IdentifiableGoal {
        private final net.minecraft.world.entity.Mob mob;
        private int idleTime;

        public IdleGoal(net.minecraft.world.entity.Mob mob) {
            this.mob = mob;
        }

        @Override
        public String getGoalId() {
            return "idle";
        }

        @Override
        public boolean canUse() {
            return mob.getRandom().nextInt(200) == 0;
        }

        @Override
        public void start() {
            idleTime = 40 + mob.getRandom().nextInt(80); // 2-6 seconds
            mob.getNavigation().stop();
        }

        @Override
        public boolean canContinueToUse() {
            return idleTime > 0;
        }

        @Override
        public void tick() {
            idleTime--;
        }
    }

    /**
     * Flee from player goal for passive mobs
     */
    public static class FleeFromPlayerGoal extends Goal implements IdentifiableGoal {
        private final net.minecraft.world.entity.Mob mob;
        private Player fleeTarget;
        private final double fleeDistance;
        private final double fleeSpeed;

        public FleeFromPlayerGoal(net.minecraft.world.entity.Mob mob, double fleeDistance, double fleeSpeed) {
            this.mob = mob;
            this.fleeDistance = fleeDistance;
            this.fleeSpeed = fleeSpeed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public String getGoalId() {
            return "flee_from_player";
        }

        @Override
        public boolean canUse() {
            fleeTarget = mob.level().getNearestPlayer(mob, fleeDistance);
            return fleeTarget != null && !fleeTarget.isCreative() && !fleeTarget.isSpectator();
        }

        @Override
        public void start() {
            double deltaX = mob.getX() - fleeTarget.getX();
            double deltaZ = mob.getZ() - fleeTarget.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            if (distance > 0) {
                double fleeX = mob.getX() + (deltaX / distance) * 8.0;
                double fleeZ = mob.getZ() + (deltaZ / distance) * 8.0;
                mob.getNavigation().moveTo(fleeX, mob.getY(), fleeZ, fleeSpeed);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return fleeTarget != null &&
                    mob.distanceToSqr(fleeTarget) < fleeDistance * fleeDistance &&
                    !mob.getNavigation().isDone();
        }

        @Override
        public void stop() {
            fleeTarget = null;
            mob.getNavigation().stop();
        }
    }

    /**
     * Attack player goal for aggressive mobs with visibility checks
     */
    public static class AttackPlayerGoal extends Goal implements IdentifiableGoal {
        private final net.minecraft.world.entity.Mob mob;
        private final double speed;
        private final double range;
        private Player target;
        private int cooldown;
        private int lostTargetTime;

        public AttackPlayerGoal(net.minecraft.world.entity.Mob mob, double speed, double range) {
            this.mob = mob;
            this.speed = speed;
            this.range = range;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public String getGoalId() {
            return "attack_player";
        }

        @Override
        public boolean canUse() {
            if (cooldown > 0) {
                cooldown--;
                return false;
            }

            target = mob.level().getNearestPlayer(mob, range);
            if (target == null || target.isCreative() || target.isSpectator()) {
                return false;
            }

            // Visibility check - ensure mob can see the player
            return mob.getSensing().hasLineOfSight(target);
        }

        @Override
        public void start() {
            mob.getNavigation().moveTo(target, speed);
            cooldown = 20; // 1 second cooldown
            lostTargetTime = 0;
        }

        @Override
        public boolean canContinueToUse() {
            if (target == null || target.isCreative() || target.isSpectator()) {
                return false;
            }

            // Continue if target is within range and visible
            if (mob.distanceToSqr(target) <= range * range && mob.getSensing().hasLineOfSight(target)) {
                lostTargetTime = 0;
                return true;
            }

            // Allow brief periods where target is not visible (natural behavior)
            lostTargetTime++;
            return lostTargetTime < 60; // 3 seconds grace period
        }

        @Override
        public void tick() {
            if (target != null) {
                mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

                if (mob.distanceToSqr(target) < 4.0D) {
                    // Attack logic
                    mob.doHurtTarget((ServerLevel) mob.level(), target);
                } else {
                    mob.getNavigation().moveTo(target, speed);
                }
            }
        }

        @Override
        public void stop() {
            target = null;
            lostTargetTime = 0;
            mob.getNavigation().stop();
        }
    }
}