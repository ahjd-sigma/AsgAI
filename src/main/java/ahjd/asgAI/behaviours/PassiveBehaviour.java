package ahjd.asgAI.behaviours;

import ahjd.asgAI.goals.CustomPassiveGoal;
import ahjd.asgAI.utils.BehaviourEnums;
import ahjd.asgAI.utils.MobBehaviour;
import ahjd.asgAI.utils.NMSHelper;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityTargetEvent;

public class PassiveBehaviour implements MobBehaviour {

    @Override
    public void onApply(Mob mob) {
        NMSHelper.clearAllGoals(mob);
        NMSHelper.addCustomGoal(mob, new CustomPassiveGoal(
                NMSHelper.getNMSMob(mob)
        ), BehaviourEnums.BehaviourPriority.NORMAL);
    }

    @Override
    public void onTarget(Mob mob, EntityTargetEvent event) {
        event.setCancelled(true);
        mob.setTarget(null);
    }

    @Override
    public void onRemove(Mob mob) {
        NMSHelper.removeCustomGoals(mob, "passive");
    }

    @Override
    public BehaviourEnums.BehaviourType getType() {
        return BehaviourEnums.BehaviourType.PASSIVE;
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