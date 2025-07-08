package ahjd.asgAI.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CustomNeutralGoal extends Goal {
    private final Mob mob;
    private final double wanderRange;
    private int wanderCooldown;
    private double targetX, targetY, targetZ;

    public CustomNeutralGoal(Mob mob) {
        this.mob = mob;
        this.wanderRange = 10.0;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
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

        mob.getNavigation().moveTo(targetX, targetY, targetZ, 0.8);
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
