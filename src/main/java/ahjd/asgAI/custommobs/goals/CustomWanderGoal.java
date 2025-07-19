package ahjd.asgAI.custommobs.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class CustomWanderGoal implements CustomGoal {
    private final double speedModifier;
    private final float probability;
    
    public CustomWanderGoal() {
        this(1.0, 0.001f);
    }
    
    public CustomWanderGoal(double speedModifier) {
        this(speedModifier, 0.001f);
    }
    
    public CustomWanderGoal(double speedModifier, float probability) {
        this.speedModifier = speedModifier;
        this.probability = probability;
    }
    
    @Override
    public Goal createNMSGoal(Mob mob) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            return new WaterAvoidingRandomStrollGoal(pathfinderMob, speedModifier, probability);
        }
        return null;
    }
    
    @Override
    public String getName() {
        return "custom_wander";
    }
    
    @Override
    public String getDescription() {
        return "Makes the mob wander around randomly when not doing other tasks";
    }
    
    @Override
    public boolean canApplyTo(Mob mob) {
        return mob instanceof PathfinderMob;
    }
}