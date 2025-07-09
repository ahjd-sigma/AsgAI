package ahjd.asgAI.behaviours;


import ahjd.asgAI.goals.CustomAggressiveGoal;
import ahjd.asgAI.utils.BehaviourEnums;
import ahjd.asgAI.utils.MobBehaviour;
import ahjd.asgAI.utils.NMSHelper;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AggressiveBehaviour implements MobBehaviour {

    @Override
    public void onApply(Mob mob) {
        NMSHelper.clearAllGoals(mob);
        NMSHelper.addCustomGoal(mob, new CustomAggressiveGoal(
                NMSHelper.getNMSMob(mob), 1.0, 16.0
        ), BehaviourEnums.BehaviourPriority.HIGH);
    }

    @Override
    public void onRemove(Mob mob) {
        NMSHelper.removeCustomGoals(mob, "aggressive");
    }

    @Override
    public void onDamage(Mob mob, EntityDamageByEntityEvent event) {
        // Make mob more aggressive when damaged
        if (event.getDamager() instanceof org.bukkit.entity.Player) {
            mob.setTarget((org.bukkit.entity.LivingEntity) event.getDamager());
        }
    }

    @Override
    public BehaviourEnums.BehaviourType getType() {
        return BehaviourEnums.BehaviourType.AGGRESSIVE;
    }

    @Override
    public String getId() {
        return "aggressive";
    }

    @Override
    public boolean isApplicable(Mob mob) {
        return true; // Can be applied to any mob
    }
}
