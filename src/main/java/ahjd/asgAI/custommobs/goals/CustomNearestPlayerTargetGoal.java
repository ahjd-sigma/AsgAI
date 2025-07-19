package ahjd.asgAI.custommobs.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class CustomNearestPlayerTargetGoal implements CustomGoal {
    private final boolean mustSee;
    private final boolean mustReach;
    private final int randomInterval;
    
    public CustomNearestPlayerTargetGoal() {
        this(true, true, 0);
    }
    
    public CustomNearestPlayerTargetGoal(boolean mustSee) {
        this(mustSee, true, 0);
    }
    
    public CustomNearestPlayerTargetGoal(boolean mustSee, boolean mustReach, int randomInterval) {
        this.mustSee = mustSee;
        this.mustReach = mustReach;
        this.randomInterval = randomInterval;
    }
    
    @Override
    public Goal createNMSGoal(Mob mob) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            return new NearestAttackableTargetGoal<>(
                pathfinderMob, 
                Player.class, 
                randomInterval, 
                mustSee, 
                mustReach, 
                null
            );
        }
        return null;
    }
    
    @Override
    public String getName() {
        return "custom_nearest_player_target";
    }
    
    @Override
    public String getDescription() {
        return "Makes the mob target the nearest player within range";
    }
    
    @Override
    public boolean canApplyTo(Mob mob) {
        return mob instanceof PathfinderMob;
    }
}