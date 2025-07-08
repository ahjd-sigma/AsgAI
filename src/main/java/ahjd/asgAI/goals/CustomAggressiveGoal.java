package ahjd.asgAI.goals;


import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import java.util.EnumSet;

public class CustomAggressiveGoal extends Goal {
    private final Mob mob;
    private final double speed;
    private final double range;
    private Player target;
    private int cooldown;

    public CustomAggressiveGoal(Mob mob, double speed, double range) {
        this.mob = mob;
        this.speed = speed;
        this.range = range;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        target = mob.level().getNearestPlayer(mob, range);
        return target != null && !target.isCreative() && !target.isSpectator();
    }

    @Override
    public void start() {
        mob.getNavigation().moveTo(target, speed);
        cooldown = 20; // 1 second cooldown
    }

    @Override
    public void tick() {
        if (target != null) {
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (mob.distanceToSqr(target) < 4.0D) {
                // Attack logic would go here
                mob.doHurtTarget((ServerLevel) mob.level(), target);
            } else {
                mob.getNavigation().moveTo(target, speed);
            }
        }
    }

    @Override
    public void stop() {
        target = null;
        mob.getNavigation().stop();
    }
}