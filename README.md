# AsgAI - Custom Mob System

A powerful Bukkit/Spigot plugin that allows you to create fully customizable mobs with custom AI goals and sensors.

## Features

- **Custom Mob Templates**: Define reusable mob configurations with custom attributes
- **Custom AI Goals**: Implement custom behaviors like attacking, chasing, wandering, defending areas, and patrolling
- **Custom Sensors**: Add intelligent detection systems for players, hostiles, and threats
- **Dynamic Spawning**: Spawn custom mobs anywhere with simple commands
- **Management Commands**: Full control over active custom mobs

## Installation

1. Place the plugin JAR file in your server's `plugins` folder
2. Restart your server
3. The plugin will automatically create default mob templates

## Commands

### Main Command: `/custommob`

- `/custommob spawn <template> [world] [x] [y] [z]` - Spawn a custom mob
- `/custommob list` - List all active custom mobs
- `/custommob info <template>` - Show template information
- `/custommob remove <radius>` - Remove custom mobs within radius
- `/custommob cleanup` - Clean up invalid mob instances
- `/custommob templates` - List available templates

## Default Templates

### Aggressive Zombie
- **ID**: `aggressive_zombie`
- **Base**: Zombie
- **Health**: 30.0
- **Speed**: 0.3
- **Attack Damage**: 5.0
- **Goals**: Attack players, chase attackers, wander randomly
- **Sensors**: Player detection, threat assessment

### Guard Zombie
- **ID**: `guard_zombie`
- **Base**: Zombie
- **Health**: 40.0
- **Speed**: 0.25
- **Attack Damage**: 6.0
- **Goals**: Defend area, patrol, attack hostiles
- **Sensors**: Player detection, hostile detection, threat assessment

## Custom Goals Available

1. **CustomAttackGoal** - Melee attack behavior
2. **CustomChaseGoal** - Chase entities that hurt the mob
3. **CustomWanderGoal** - Random wandering
4. **CustomNearestPlayerTargetGoal** - Target nearest player
5. **CustomDefendAreaGoal** - Defend a specific area
6. **CustomPatrolGoal** - Patrol between points
7. **CustomNearestHostileTargetGoal** - Target hostile entities

## Custom Sensors Available

1. **CustomPlayerSensor** - Detect nearby players
2. **CustomHostileSensor** - Detect hostile entities
3. **CustomThreatSensor** - Advanced threat assessment

## Examples

### Spawning Mobs
```
# Spawn an aggressive zombie at your location
/custommob spawn aggressive_zombie

# Spawn a guard zombie at specific coordinates
/custommob spawn guard_zombie world 100 64 200
```

### Managing Mobs
```
# List all active custom mobs
/custommob list

# Remove all custom mobs within 10 blocks
/custommob remove 10

# Clean up any invalid mob instances
/custommob cleanup
```

## Permissions

- `asgai.custommob.*` - All custom mob permissions
- `asgai.custommob.spawn` - Spawn custom mobs
- `asgai.custommob.manage` - Manage custom mobs (list, remove, cleanup)
- `asgai.custommob.info` - View template information

## Technical Details

### Architecture
- **CustomMobManager**: Central management system
- **CustomMobTemplate**: Mob configuration definitions
- **CustomMobInstance**: Active mob tracking
- **CustomGoal Interface**: Extensible goal system
- **CustomSensor Interface**: Extensible sensor system

### NMS Integration
- Uses decompiled NMS classes for deep AI integration
- Compatible with Paper/Spigot servers
- Efficient goal and sensor management
- Automatic cleanup of invalid instances

## Development

To extend the system:

1. Implement `CustomGoal` for new behaviors
2. Implement `CustomSensor` for new detection systems
3. Register new templates in `CustomMobManager`
4. Test thoroughly with different mob types

## Support

For issues, suggestions, or contributions, please refer to the plugin documentation or contact the development team.