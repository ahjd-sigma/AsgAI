package ahjd.asgAI.goals;


import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class CustomPassiveGoal extends Goal {
    private final Mob mob;
    private Player fleeTarget;
    private final double fleeDistance;
    private final double fleeSpeed;

    public CustomPassiveGoal(Mob mob) {
        this.mob = mob;
        this.fleeDistance = 12.0;
        this.fleeSpeed = 2.2;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        fleeTarget = mob.level().getNearestPlayer(mob, fleeDistance);
        return fleeTarget != null && !fleeTarget.isCreative() && !fleeTarget.isSpectator();
    }

    @Override
    public void start() {
        // Calculate flee direction (opposite of player)
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