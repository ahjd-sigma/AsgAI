package ahjd.asgAI.custommobs.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CustomPatrolGoal implements CustomGoal {
    private final double speedModifier;
    private final int patrolRadius;
    private final int patrolPoints;
    
    public CustomPatrolGoal() {
        this(1.0, 20, 4);
    }
    
    public CustomPatrolGoal(double speedModifier, int patrolRadius, int patrolPoints) {
        this.speedModifier = speedModifier;
        this.patrolRadius = patrolRadius;
        this.patrolPoints = patrolPoints;
    }
    
    @Override
    public Goal createNMSGoal(Mob mob) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            return new PatrolGoalImpl(pathfinderMob, speedModifier, patrolRadius, patrolPoints);
        }
        return null;
    }
    
    @Override
    public String getName() {
        return "custom_patrol";
    }
    
    @Override
    public String getDescription() {
        return "Makes the mob patrol between multiple points in an area";
    }
    
    @Override
    public boolean canApplyTo(Mob mob) {
        return mob instanceof PathfinderMob;
    }
    
    private static class PatrolGoalImpl extends Goal {
        private final PathfinderMob mob;
        private final double speedModifier;
        private final int patrolRadius;
        private final List<BlockPos> patrolPoints;
        private int currentPatrolIndex;
        private int idleTime;
        private final int maxIdleTime = 100; // 5 seconds at 20 TPS
        
        public PatrolGoalImpl(PathfinderMob mob, double speedModifier, int patrolRadius, int patrolPointCount) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.patrolRadius = patrolRadius;
            this.patrolPoints = new ArrayList<>();
            this.currentPatrolIndex = 0;
            this.idleTime = 0;
            
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            
            // Generate patrol points around spawn location
            generatePatrolPoints(mob.blockPosition(), patrolPointCount);
        }
        
        private void generatePatrolPoints(BlockPos center, int pointCount) {
            patrolPoints.clear();
            
            for (int i = 0; i < pointCount; i++) {
                double angle = (2 * Math.PI * i) / pointCount;
                int x = (int) (center.getX() + Math.cos(angle) * patrolRadius);
                int z = (int) (center.getZ() + Math.sin(angle) * patrolRadius);
                int y = mob.level().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, 
                    new BlockPos(x, center.getY(), z)).getY();
                
                patrolPoints.add(new BlockPos(x, y, z));
            }
        }
        
        @Override
        public boolean canUse() {
            // Only patrol if mob has no target
            return mob.getTarget() == null && !patrolPoints.isEmpty();
        }
        
        @Override
        public boolean canContinueToUse() {
            return mob.getTarget() == null && !patrolPoints.isEmpty();
        }
        
        @Override
        public void start() {
            idleTime = 0;
        }
        
        @Override
        public void stop() {
            mob.getNavigation().stop();
        }
        
        @Override
        public void tick() {
            if (patrolPoints.isEmpty()) return;
            
            BlockPos currentTarget = patrolPoints.get(currentPatrolIndex);
            
            // Check if we've reached the current patrol point
            if (mob.distanceToSqr(currentTarget.getX(), currentTarget.getY(), currentTarget.getZ()) < 4.0) {
                // Wait at patrol point
                idleTime++;
                
                if (idleTime >= maxIdleTime) {
                    // Move to next patrol point
                    currentPatrolIndex = (currentPatrolIndex + 1) % patrolPoints.size();
                    idleTime = 0;
                }
            } else {
                // Move towards current patrol point
                Path path = mob.getNavigation().createPath(currentTarget, 1);
                if (path != null) {
                    mob.getNavigation().moveTo(path, speedModifier);
                }
            }
        }
    }
}