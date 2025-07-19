package ahjd.asgAI.custommobs.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class CustomAttackGoal implements CustomGoal {
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    
    public CustomAttackGoal() {
        this(1.0, false);
    }
    
    public CustomAttackGoal(double speedModifier, boolean followingTargetEvenIfNotSeen) {
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
    }
    
    @Override
    public Goal createNMSGoal(Mob mob) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            return new MeleeAttackGoal(pathfinderMob, speedModifier, followingTargetEvenIfNotSeen);
        }
        return null;
    }
    
    @Override
    public String getName() {
        return "custom_attack";
    }
    
    @Override
    public String getDescription() {
        return "Makes the mob attack nearby targets with melee attacks";
    }
    
    @Override
    public boolean canApplyTo(Mob mob) {
        return mob instanceof PathfinderMob;
    }
}