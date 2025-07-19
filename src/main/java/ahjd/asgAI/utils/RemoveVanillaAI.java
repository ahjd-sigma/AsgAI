package ahjd.asgAI.utils;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

public class RemoveVanillaAI {

    public static void clearVanillaAI(LivingEntity entity) {
        if (!(entity instanceof CraftLivingEntity craftEntity)) return;

        Mob nmsMob = (Mob) craftEntity.getHandle();

        GoalSelector goalSelector = nmsMob.goalSelector;
        GoalSelector targetSelector = nmsMob.targetSelector;

        // Remove all goals by passing a predicate that returns true for everything
        goalSelector.removeAllGoals(goal -> true);
        targetSelector.removeAllGoals(goal -> true);
    }
}
