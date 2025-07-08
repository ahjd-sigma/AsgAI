package ahjd.asgAI.behaviours;

import ahjd.asgAI.goals.CustomPassiveGoal;
import ahjd.asgAI.utils.BehaviourEnums;
import ahjd.asgAI.utils.MobBehaviour;
import ahjd.asgAI.utils.NMSHelper;
import org.bukkit.entity.Mob;

public class PassiveBehaviour implements MobBehaviour {

    @Override
    public void onApply(Mob mob) {
        NMSHelper.clearAllGoals(mob);
        NMSHelper.addCustomGoal(mob, new CustomPassiveGoal(
                NMSHelper.getNMSMob(mob)
        ), BehaviourEnums.BehaviourPriority.NORMAL);
        mob.setTarget(null); // prevent chasing
        mob.setPersistent(true); //type shi
    }

    @Override
    public void onRemove(Mob mob) {
        NMSHelper.removeCustomGoals(mob, "passive");
    }

    @Override
    public BehaviourEnums.BehaviourType getType() {
        return BehaviourEnums.BehaviourType.NEUTRAL;
    }

    @Override
    public String getId() {
        return "passive";
    }

    @Override
    public boolean isApplicable(Mob mob) {
        return true;
    }
}