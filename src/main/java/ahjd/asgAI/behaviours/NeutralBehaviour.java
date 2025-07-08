package ahjd.asgAI.behaviours;


import ahjd.asgAI.MobAIManager;
import ahjd.asgAI.goals.CustomNeutralGoal;
import ahjd.asgAI.utils.BehaviourEnums;
import ahjd.asgAI.utils.MobBehaviour;
import ahjd.asgAI.utils.NMSHelper;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NeutralBehaviour implements MobBehaviour {

    @Override
    public void onApply(Mob mob) {
        NMSHelper.addCustomGoal(mob, new CustomNeutralGoal(
                NMSHelper.getNMSMob(mob)
        ), BehaviourEnums.BehaviourPriority.NORMAL);
    }

    @Override
    public void onRemove(Mob mob) {
        NMSHelper.removeCustomGoals(mob, "neutral");
    }

    @Override
    public void onDamage(Mob mob, EntityDamageByEntityEvent event) {
        // Switch to aggressive when attacked
        MobAIManager.getInstance().changeBehavior(mob, "aggressive");
    }

    @Override
    public BehaviourEnums.BehaviourType getType() {
        return BehaviourEnums.BehaviourType.NEUTRAL;
    }

    @Override
    public String getId() {
        return "neutral";
    }

    @Override
    public boolean isApplicable(Mob mob) {
        return true;
    }
}