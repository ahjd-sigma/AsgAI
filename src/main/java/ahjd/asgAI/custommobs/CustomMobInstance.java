package ahjd.asgAI.custommobs;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomMobInstance {
    private final LivingEntity entity;
    private final CustomMobTemplate template;
    private final Map<String, Object> instanceData;
    private final long spawnTime;
    private boolean isActive;
    
    public CustomMobInstance(LivingEntity entity, CustomMobTemplate template) {
        this.entity = entity;
        this.template = template;
        this.instanceData = new HashMap<>();
        this.spawnTime = System.currentTimeMillis();
        this.isActive = true;
    }
    
    public LivingEntity getEntity() {
        return entity;
    }
    
    public CustomMobTemplate getTemplate() {
        return template;
    }
    
    public UUID getEntityId() {
        return entity.getUniqueId();
    }
    
    public String getTemplateId() {
        return template.getId();
    }
    
    public long getSpawnTime() {
        return spawnTime;
    }
    
    public long getAgeInMillis() {
        return System.currentTimeMillis() - spawnTime;
    }
    
    public boolean isActive() {
        return isActive && entity.isValid() && !entity.isDead();
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public void setInstanceData(String key, Object value) {
        instanceData.put(key, value);
    }
    
    public Object getInstanceData(String key) {
        return instanceData.get(key);
    }
    
    public <T> T getInstanceData(String key, Class<T> type) {
        Object value = instanceData.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }
    
    public Map<String, Object> getAllInstanceData() {
        return new HashMap<>(instanceData);
    }
    
    public void removeInstanceData(String key) {
        instanceData.remove(key);
    }
    
    public boolean hasInstanceData(String key) {
        return instanceData.containsKey(key);
    }
    
    @Override
    public String toString() {
        return "CustomMobInstance{" +
                "entityId=" + getEntityId() +
                ", templateId='" + getTemplateId() + '\'' +
                ", spawnTime=" + spawnTime +
                ", isActive=" + isActive() +
                '}';
    }
}