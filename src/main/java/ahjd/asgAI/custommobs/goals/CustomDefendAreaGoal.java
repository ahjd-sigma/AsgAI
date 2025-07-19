package ahjd.asgAI.custommobs.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CustomDefendAreaGoal implements CustomGoal {
    private final double defendRadius;
    private final double speedModifier;
    
    public CustomDefendAreaGoal() {
        this(16.0, 1.0);
    }
    
    public CustomDefendAreaGoal(double defendRadius, double speedModifier) {
        this.defendRadius = defendRadius;
        this.speedModifier = speedModifier;
    }
    
    @Override
    public Goal createNMSGoal(Mob mob) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            return new DefendAreaGoalImpl(pathfinderMob, defendRadius, speedModifier);
        }
        return null;
    }
    
    @Override
    public String getName() {
        return "custom_defend_area";
    }
    
    @Override
    public String getDescription() {
        return "Makes the mob defend a specific area around its spawn point";
    }
    
    @Override
    public boolean canApplyTo(Mob mob) {
        return mob instanceof PathfinderMob;
    }
    
    private static class DefendAreaGoalImpl extends Goal {
        private final PathfinderMob mob;
        private final double defendRadius;
        private final double speedModifier;
        private BlockPos defendCenter;
        private LivingEntity target;
        
        public DefendAreaGoalImpl(PathfinderMob mob, double defendRadius, double speedModifier) {
            this.mob = mob;
            this.defendRadius = defendRadius;
            this.speedModifier = speedModifier;
            this.defendCenter = mob.blockPosition();
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.TARGET));
        }
        
        @Override
        public boolean canUse() {
            // Look for threats near the defend area
            LivingEntity nearestThreat = mob.level().getNearestPlayer(
                defendCenter.getX(), defendCenter.getY(), defendCenter.getZ(),
                defendRadius, false
            );
            
            if (nearestThreat != null && isValidTarget(nearestThreat)) {
                this.target = nearestThreat;
                return true;
            }
            
            return false;
        }
        
        @Override
        public boolean canContinueToUse() {
            if (target == null || !target.isAlive()) {
                return false;
            }
            
            // Stop defending if target is too far from defend area
            double distanceToDefendCenter = target.distanceToSqr(
                defendCenter.getX(), defendCenter.getY(), defendCenter.getZ()
            );
            
            return distanceToDefendCenter <= defendRadius * defendRadius;
        }
        
        @Override
        public void start() {
            mob.setTarget(target);
        }
        
        @Override
        public void stop() {
            mob.setTarget(null);
            target = null;
        }
        
        @Override
        public void tick() {
            if (target != null) {
                mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                
                double distanceToTarget = mob.distanceToSqr(target);
                if (distanceToTarget < 4.0) {
                    mob.getNavigation().stop();
                } else {
                    mob.getNavigation().moveTo(target, speedModifier);
                }
            }
        }
        
        private boolean isValidTarget(LivingEntity entity) {
            return entity instanceof Player player && !entity.isSpectator() && !player.isCreative();
        }
    }
}