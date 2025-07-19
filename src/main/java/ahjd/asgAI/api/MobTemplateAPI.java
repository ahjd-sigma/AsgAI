package ahjd.asgAI.api;

import ahjd.asgAI.custommobs.CustomMobManager;
import ahjd.asgAI.custommobs.CustomMobTemplate;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * API class for spawning registered AI mob templates by ID.
 * Usage: MobTemplateAPI.spawnMob("aggressive_zombie", location);
 */
public class MobTemplateAPI {
    /**
     * Spawns a registered mob template at the given location.
     * @param templateId The template ID (e.g. "aggressive_zombie" or "guard_zombie")
     * @param location The Bukkit location to spawn at
     * @return The spawned LivingEntity, or null if template not found
     */
    @Nullable
    public static LivingEntity spawnMob(String templateId, Location location) {
        CustomMobManager manager = CustomMobManager.getInstance();
        if (manager == null) return null;
        try {
            return manager.spawnCustomMob(templateId, location);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Checks if a mob template with the given ID exists.
     * @param templateId The template ID
     * @return true if the template exists
     */
    public static boolean hasTemplate(String templateId) {
        CustomMobManager manager = CustomMobManager.getInstance();
        return manager != null && manager.getTemplate(templateId) != null;
    }

    /**
     * Gets the template object for advanced usage.
     * @param templateId The template ID
     * @return The CustomMobTemplate, or null if not found
     */
    @Nullable
    public static CustomMobTemplate getTemplate(String templateId) {
        CustomMobManager manager = CustomMobManager.getInstance();
        return manager != null ? manager.getTemplate(templateId) : null;
    }
}
