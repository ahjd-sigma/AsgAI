package ahjd.asgAI.custommobs.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class CustomChaseGoal implements CustomGoal {
    private final Class<?>[] toIgnoreDamage;
    private final boolean alertOthers;
    
    public CustomChaseGoal() {
        this(true);
    }
    
    public CustomChaseGoal(boolean alertOthers, Class<?>... toIgnoreDamage) {
        this.alertOthers = alertOthers;
        this.toIgnoreDamage = toIgnoreDamage;
    }
    
    @Override
    public Goal createNMSGoal(Mob mob) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            HurtByTargetGoal goal = new HurtByTargetGoal(pathfinderMob, toIgnoreDamage);
            if (alertOthers) {
                goal.setAlertOthers();
            }
            return goal;
        }
        return null;
    }
    
    @Override
    public String getName() {
        return "custom_chase";
    }
    
    @Override
    public String getDescription() {
        return "Makes the mob chase and target entities that hurt it";
    }
    
    @Override
    public boolean canApplyTo(Mob mob) {
        return mob instanceof PathfinderMob;
    }
}