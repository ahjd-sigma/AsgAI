package ahjd.asgAI.custommobs;

import ahjd.asgAI.custommobs.goals.CustomGoal;
import ahjd.asgAI.custommobs.sensors.CustomSensor;
import org.bukkit.entity.EntityType;

import java.util.*;

public class CustomMobTemplate {
    private final String id;
    private EntityType baseEntity;
    private String displayName;
    private Double health;
    private Double speed;
    private Double attackDamage;
    private Double followRange;
    private Double armor;
    private Double armorToughness;
    private Double knockbackResistance;
    
    private final Map<Integer, CustomGoal> goals;
    private final Map<Integer, CustomGoal> targetGoals;
    private final List<CustomSensor> sensors;
    private final Map<String, Object> customData;
    
    public CustomMobTemplate(String id) {
        this.id = id;
        this.goals = new TreeMap<>();
        this.targetGoals = new TreeMap<>();
        this.sensors = new ArrayList<>();
        this.customData = new HashMap<>();
    }
    
    // Builder pattern methods
    public CustomMobTemplate setBaseEntity(EntityType baseEntity) {
        this.baseEntity = baseEntity;
        return this;
    }
    
    public CustomMobTemplate setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public CustomMobTemplate setHealth(Double health) {
        this.health = health;
        return this;
    }
    
    public CustomMobTemplate setSpeed(Double speed) {
        this.speed = speed;
        return this;
    }
    
    public CustomMobTemplate setAttackDamage(Double attackDamage) {
        this.attackDamage = attackDamage;
        return this;
    }
    
    public CustomMobTemplate setFollowRange(Double followRange) {
        this.followRange = followRange;
        return this;
    }
    
    public CustomMobTemplate setArmor(Double armor) {
        this.armor = armor;
        return this;
    }
    
    public CustomMobTemplate setArmorToughness(Double armorToughness) {
        this.armorToughness = armorToughness;
        return this;
    }
    
    public CustomMobTemplate setKnockbackResistance(Double knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return this;
    }
    
    public CustomMobTemplate addGoal(int priority, CustomGoal goal) {
        this.goals.put(priority, goal);
        return this;
    }
    
    public CustomMobTemplate addTargetGoal(int priority, CustomGoal goal) {
        this.targetGoals.put(priority, goal);
        return this;
    }
    
    public CustomMobTemplate addSensor(CustomSensor sensor) {
        this.sensors.add(sensor);
        return this;
    }
    
    public CustomMobTemplate setCustomData(String key, Object value) {
        this.customData.put(key, value);
        return this;
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public EntityType getBaseEntity() {
        return baseEntity;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Double getHealth() {
        return health;
    }
    
    public Double getSpeed() {
        return speed;
    }
    
    public Double getAttackDamage() {
        return attackDamage;
    }
    
    public Double getFollowRange() {
        return followRange;
    }
    
    public Double getArmor() {
        return armor;
    }
    
    public Double getArmorToughness() {
        return armorToughness;
    }
    
    public Double getKnockbackResistance() {
        return knockbackResistance;
    }
    
    public Map<Integer, CustomGoal> getGoals() {
        return new TreeMap<>(goals);
    }
    
    public Map<Integer, CustomGoal> getTargetGoals() {
        return new TreeMap<>(targetGoals);
    }
    
    public List<CustomSensor> getSensors() {
        return new ArrayList<>(sensors);
    }
    
    public Map<String, Object> getCustomData() {
        return new HashMap<>(customData);
    }
    
    public Object getCustomData(String key) {
        return customData.get(key);
    }
    
    @Override
    public String toString() {
        return "CustomMobTemplate{" +
                "id='" + id + '\'' +
                ", baseEntity=" + baseEntity +
                ", displayName='" + displayName + '\'' +
                ", health=" + health +
                ", speed=" + speed +
                ", attackDamage=" + attackDamage +
                ", followRange=" + followRange +
                '}';
    }
}