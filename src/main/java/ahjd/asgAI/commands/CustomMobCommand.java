package ahjd.asgAI.commands;

import ahjd.asgAI.AsgAI;
import ahjd.asgAI.custommobs.CustomMobInstance;
import ahjd.asgAI.custommobs.CustomMobManager;
import ahjd.asgAI.custommobs.CustomMobTemplate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomMobCommand implements CommandExecutor, TabCompleter {
    private final CustomMobManager mobManager;
    private final AsgAI plugin;
    
    public CustomMobCommand(CustomMobManager mobManager, AsgAI plugin) {
        this.mobManager = mobManager;
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "spawn":
                return handleSpawn(sender, args);
            case "list":
                return handleList(sender, args);
            case "info":
                return handleInfo(sender, args);
            case "remove":
                return handleRemove(sender, args);
            case "cleanup":
                return handleCleanup(sender, args);
            case "templates":
                return handleTemplates(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private boolean handleSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /custommob spawn <template> [amount]");
            return true;
        }
        
        String templateId = args[1];
        int amount = 1;
        
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0 || amount > 10) {
                    sender.sendMessage(ChatColor.RED + "Amount must be between 1 and 10!");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[2]);
                return true;
            }
        }
        
        CustomMobTemplate template = mobManager.getTemplate(templateId);
        if (template == null) {
            sender.sendMessage(ChatColor.RED + "Unknown template: " + templateId);
            sender.sendMessage(ChatColor.YELLOW + "Available templates: " + 
                String.join(", ", mobManager.getTemplateIds()));
            return true;
        }
        
        Location spawnLocation = player.getLocation();
        
        try {
            for (int i = 0; i < amount; i++) {
                // Offset spawn location slightly for multiple mobs
                Location offsetLocation = spawnLocation.clone().add(
                    (Math.random() - 0.5) * 4, 0, (Math.random() - 0.5) * 4
                );
                
                LivingEntity entity = mobManager.spawnCustomMob(templateId, offsetLocation);
                
                if (i == 0) { // Only send message for first mob
                    sender.sendMessage(ChatColor.GREEN + "Spawned " + amount + "x " + 
                        template.getDisplayName() + ChatColor.GREEN + " at your location!");
                }
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to spawn mob: " + e.getMessage());
            plugin.getLogger().warning("Failed to spawn custom mob: " + e.getMessage());
        }
        
        return true;
    }
    
    private boolean handleList(CommandSender sender, String[] args) {
        var activeMobs = mobManager.getActiveMobs();
        
        if (activeMobs.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No active custom mobs found.");
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== Active Custom Mobs ===");
        
        for (CustomMobInstance instance : activeMobs) {
            if (instance.isActive()) {
                LivingEntity entity = instance.getEntity();
                Location loc = entity.getLocation();
                
                sender.sendMessage(String.format(
                    ChatColor.WHITE + "• %s %s(ID: %s)%s at %s(%.1f, %.1f, %.1f)%s - Age: %ds",
                    instance.getTemplate().getDisplayName(),
                    ChatColor.GRAY,
                    instance.getTemplateId(),
                    ChatColor.WHITE,
                    ChatColor.GRAY,
                    loc.getX(), loc.getY(), loc.getZ(),
                    ChatColor.WHITE,
                    instance.getAgeInMillis() / 1000
                ));
            }
        }
        
        return true;
    }
    
    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /custommob info <template>");
            return true;
        }
        
        String templateId = args[1];
        CustomMobTemplate template = mobManager.getTemplate(templateId);
        
        if (template == null) {
            sender.sendMessage(ChatColor.RED + "Unknown template: " + templateId);
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== Template Info: " + templateId + " ===");
        sender.sendMessage(ChatColor.WHITE + "Display Name: " + template.getDisplayName());
        sender.sendMessage(ChatColor.WHITE + "Base Entity: " + template.getBaseEntity());
        sender.sendMessage(ChatColor.WHITE + "Health: " + template.getHealth());
        sender.sendMessage(ChatColor.WHITE + "Speed: " + template.getSpeed());
        sender.sendMessage(ChatColor.WHITE + "Attack Damage: " + template.getAttackDamage());
        sender.sendMessage(ChatColor.WHITE + "Follow Range: " + template.getFollowRange());
        
        sender.sendMessage(ChatColor.YELLOW + "Goals: " + template.getGoals().size());
        template.getGoals().forEach((priority, goal) -> 
            sender.sendMessage(ChatColor.GRAY + "  " + priority + ": " + goal.getName()));
        
        sender.sendMessage(ChatColor.YELLOW + "Target Goals: " + template.getTargetGoals().size());
        template.getTargetGoals().forEach((priority, goal) -> 
            sender.sendMessage(ChatColor.GRAY + "  " + priority + ": " + goal.getName()));
        
        sender.sendMessage(ChatColor.YELLOW + "Sensors: " + template.getSensors().size());
        template.getSensors().forEach(sensor -> 
            sender.sendMessage(ChatColor.GRAY + "  • " + sensor.getName()));
        
        return true;
    }
    
    private boolean handleRemove(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        double radius = 10.0;
        if (args.length >= 2) {
            try {
                radius = Double.parseDouble(args[1]);
                if (radius <= 0 || radius > 100) {
                    sender.sendMessage(ChatColor.RED + "Radius must be between 1 and 100!");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid radius: " + args[1]);
                return true;
            }
        }
        
        Location playerLoc = player.getLocation();
        int removed = 0;
        
        for (CustomMobInstance instance : new ArrayList<>(mobManager.getActiveMobs())) {
            if (instance.isActive()) {
                LivingEntity entity = instance.getEntity();
                if (entity.getLocation().distance(playerLoc) <= radius) {
                    entity.remove();
                    mobManager.removeCustomMob(instance.getEntityId());
                    removed++;
                }
            }
        }
        
        sender.sendMessage(ChatColor.GREEN + "Removed " + removed + " custom mobs within " + radius + " blocks.");
        return true;
    }
    
    private boolean handleCleanup(CommandSender sender, String[] args) {
        mobManager.cleanup();
        sender.sendMessage(ChatColor.GREEN + "Cleaned up dead/invalid custom mobs from tracking.");
        return true;
    }
    
    private boolean handleTemplates(CommandSender sender, String[] args) {
        var templateIds = mobManager.getTemplateIds();
        
        if (templateIds.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No templates available.");
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== Available Templates ===");
        for (String templateId : templateIds) {
            CustomMobTemplate template = mobManager.getTemplate(templateId);
            sender.sendMessage(ChatColor.WHITE + "• " + ChatColor.YELLOW + templateId + 
                ChatColor.WHITE + " - " + template.getDisplayName());
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Custom Mob Commands ===");
        sender.sendMessage(ChatColor.WHITE + "/custommob spawn <template> [amount] - Spawn custom mob(s)");
        sender.sendMessage(ChatColor.WHITE + "/custommob list - List active custom mobs");
        sender.sendMessage(ChatColor.WHITE + "/custommob info <template> - Show template information");
        sender.sendMessage(ChatColor.WHITE + "/custommob remove [radius] - Remove custom mobs near you");
        sender.sendMessage(ChatColor.WHITE + "/custommob cleanup - Clean up dead mobs from tracking");
        sender.sendMessage(ChatColor.WHITE + "/custommob templates - List available templates");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("spawn", "list", "info", "remove", "cleanup", "templates")
                .stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("spawn") || args[0].equalsIgnoreCase("info")) {
                return mobManager.getTemplateIds().stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }
        
        return new ArrayList<>();
    }
}