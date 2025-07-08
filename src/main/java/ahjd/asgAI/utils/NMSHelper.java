package ahjd.asgAI.utils;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.bukkit.craftbukkit.entity.CraftMob;

import java.lang.reflect.Field;
import java.util.Set;

public class NMSHelper {

    private static Field availableGoalsField;

    static {
        try {
            availableGoalsField = GoalSelector.class.getDeclaredField("availableGoals");
            availableGoalsField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Mob getNMSMob(org.bukkit.entity.Mob bukkitMob) {
        return ((CraftMob) bukkitMob).getHandle();
    }

    public static void addCustomGoal(org.bukkit.entity.Mob bukkitMob, Goal goal, BehaviourEnums.BehaviourPriority priority) {
        try {
            Mob nmsMob = getNMSMob(bukkitMob);
            nmsMob.goalSelector.addGoal(priority.getPriority(), goal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeCustomGoals(org.bukkit.entity.Mob bukkitMob, String identifier) {
        try {
            Mob nmsMob = getNMSMob(bukkitMob);
            GoalSelector goalSelector = nmsMob.goalSelector;

            @SuppressWarnings("unchecked")
            Set<WrappedGoal> availableGoals = (Set<WrappedGoal>) availableGoalsField.get(goalSelector);

            availableGoals.removeIf(wrappedGoal ->
                    wrappedGoal.getGoal().getClass().getSimpleName().toLowerCase().contains(identifier)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearAllGoals(org.bukkit.entity.Mob bukkitMob) {
        try {
            Mob nmsMob = getNMSMob(bukkitMob);
            GoalSelector goalSelector = nmsMob.goalSelector;

            @SuppressWarnings("unchecked")
            Set<WrappedGoal> availableGoals = (Set<WrappedGoal>) availableGoalsField.get(goalSelector);

            availableGoals.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
