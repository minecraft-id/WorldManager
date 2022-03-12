package worldmanager.net.minecraftid.Commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import worldmanager.net.minecraftid.Utils.Config;
import worldmanager.net.minecraftid.Utils.Format;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WorldManagerCommand {

    Config config = new Config();
    MultiverseCore mvcore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
    MVWorldManager mvWorldManager = mvcore.getMVWorldManager();
    RegisteredServiceProvider<LuckPerms> provider =  Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    LuckPerms luckPerms = provider.getProvider();;

    public void initCommand() {
        new CommandAPICommand("worldmanager")
                .withAliases("wm")
                .withSubcommand(new CommandAPICommand("help")
                        .withAliases("version")
                        .executes((sender,args) -> {
                            sender.sendMessage(Format.chat(
                                    "&fAbout " + Config.getPluginDes().getName() +
                                    "\n&7Version: " + Config.getPluginDes().getVersion() +
                                    "\n&7Source: " + Config.getPluginDes().getWebsite() +
                                    "\n&7Author: " + Config.getPluginDes().getAuthors() +
                                    "\n&fFor commands, you can use /help worldmanager\n"
                            ));
                        })
                )
                .withSubcommand(new CommandAPICommand("trust")
                        .withPermission("worldmanager.trust")
                        .withSubcommand(new CommandAPICommand("player")
                                .withPermission("worldmanager.trust.player")
                                .withArguments(CommandArgument.player("all"))
                                .withArguments(CommandArgument.worldManager("bukkitWorld"))
                                .executes((sender,args)->{
                                    sender.sendMessage(trustPlayer(args[0].toString(),args[1].toString()));
                                })
                        )
                        .withSubcommand(new CommandAPICommand("group")
                                .withPermission("worldmanager.trust.group")
                                .withArguments(CommandArgument.luckPermsGroup("all"))
                                .withArguments(CommandArgument.worldManager("bukkitWorld"))
                                .executes((sender,args)->{
                                    sender.sendMessage(trustGroup(args[0].toString(),args[1].toString()));
                                })
                        )
                )
                .withSubcommand(new CommandAPICommand("untrust")
                        .withPermission("worldmanager.trust")
                        .withSubcommand(new CommandAPICommand("player")
                                .withPermission("worldmanager.trust.player")
                                .withArguments(CommandArgument.player("all"))
                                .withArguments(CommandArgument.worldManager("bukkitWorld"))
                                .executes((sender,args)->{
                                    sender.sendMessage(unTrustPlayer(args[0].toString(),args[1].toString()));
                                })
                        )
                        .withSubcommand(new CommandAPICommand("group")
                                .withPermission("worldmanager.trust.group")
                                .withArguments(CommandArgument.luckPermsGroup("all"))
                                .withArguments(CommandArgument.worldManager("bukkitWorld"))
                                .executes((sender,args)->{
                                    sender.sendMessage(unTrustGroup(args[0].toString(),args[1].toString()));
                                })
                        )
                )
                .withSubcommand(new CommandAPICommand("globalworld")
                        .withAliases("gw")
                        .withPermission("worldmanager.globalworld")
                        .withSubcommand(new CommandAPICommand("add")
                                .withArguments(CommandArgument.worldManager("bukkitWorld"))
                                .executes((sender,args)->{
                                    String worldName = args[0].toString();
                                    sender.sendMessage(Format.chat(addGlobalWorld(worldName)));
                                })
                        )
                        .withSubcommand(new CommandAPICommand("remove")
                                .withArguments(CommandArgument.worldManager("globalWorld"))
                                .executes((sender,args)->{
                                    String worldName = args[0].toString();
                                    sender.sendMessage(Format.chat(removeGlobalWorld(worldName)));
                                })
                        )
                )
                .withSubcommand(new CommandAPICommand("setdefault")
                        .withAliases("sd")
                        .withPermission("worldmanager.admin")
                        .withArguments(CommandArgument.worldManager("globalWorld"))
                        .executes((sender,args) ->{
                            String worldName = args[0].toString();
                            sender.sendMessage(Format.chat(setDefaultWorld(worldName)));
                        })
                )
                .withSubcommand(new CommandAPICommand("list")
                        .withAliases("ls")
                        .executesPlayer((sender,args) -> {
                            sendWorldList(sender);
                        })
                )
                .withSubcommand(new CommandAPICommand("reloadconfig")
                        .withPermission("worldmanager.admin")
                        .executes((sender,args)->{
                            Config.reload();
                            sender.sendMessage(Format.chat("Config Reloaded"));
                        })
                )
                .register();
    }


    private void sendWorldList(CommandSender sender){
        Collection<MultiverseWorld> worlds = this.mvWorldManager.getMVWorlds();
        FileConfiguration wmConfig = Config.getWorldConfig();
        List<String> worldGlobal = wmConfig.getStringList("globalWorld");
        Player player = (Player) sender;
        List<String> worldList = new ArrayList<>();
        for (MultiverseWorld world : worlds) {
            String worldAlias = world.getAlias();
            String worldEnv = world.getEnvironment().name();
            String[] text = {worldAlias,worldEnv};
            String rawText = Format.tellraw(text,"/mvtp "+ worldAlias,"You will teleported to "+ worldAlias);
            if(player.hasPermission(world.getAccessPermission()) || worldGlobal.contains(world.getName())) worldList.add(rawText);
        }
        Collections.sort(worldList);
        String cmd = "tellraw "+player.getName()+" "+ worldList.toString();
        sender.sendMessage(Format.chat("&f&lWorld Avalible &7Click to teleport\n-"));
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),cmd);
    }

    private String setDefaultWorld(String worldname){
        FileConfiguration wmConfig = Config.getWorldConfig();
        if(wmConfig.getString("defaultWorld").equals(worldname)) return "&eWorld is already a default world";
        wmConfig.set("defaultWorld", worldname);
        config.setWorldManagerConfig(wmConfig);
        if(config.save()){
            return "&a" +worldname+ " set to default world";
        }
        return "&cFailed to save config";
    }

    private String addGlobalWorld(String worldname){
        FileConfiguration wmConfig = Config.getWorldConfig();
        List<String> globalWorld = wmConfig.getStringList("globalWorld");
        if(globalWorld.contains(worldname)) return "&eWorld is already a global world";
        globalWorld.add(worldname);
        wmConfig.set("globalWorld",globalWorld);
        config.setWorldManagerConfig(wmConfig);
        if(config.save()){
            return "&aSuccess add " +worldname+ " to global world";
        }
        return "&cFailed to save config";
    }

    private String removeGlobalWorld(String worldname){
        FileConfiguration wmConfig = Config.getWorldConfig();
        List<String> globalWorld = wmConfig.getStringList("globalWorld");
        if(wmConfig.getString("defaultWorld").equals(worldname)) return "&eCant remove default world";
        globalWorld.remove(worldname);
        wmConfig.set("globalWorld",globalWorld);
        config.setWorldManagerConfig(wmConfig);
        if(config.save()){
            return "&aSuccess remove " +worldname+ " from global world";
        }
        return "&cFailed to save config";
    }

    private String trustPlayer(String playerTarget, String worldName){
        UUID userUUID = Bukkit.getOfflinePlayer(playerTarget).getUniqueId();
        String permission = mvWorldManager.getMVWorld(worldName).getAccessPermission().getName();

        UserManager userManager = luckPerms.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(userUUID);

        User target = giveMeADamnUser(userUUID);
        if(target.getCachedData().getPermissionData().checkPermission(permission).asBoolean()){
            return Format.chat("&e" +playerTarget+ " already have access to this world!");
        }

        luckPerms.getUserManager().modifyUser(userUUID, user -> {
            user.data().add(Node.builder(permission).build());
        });
        return Format.chat("&a" +playerTarget+ " now have access to this world!");
    }

    private String unTrustPlayer(String playerTarget, String worldName){
        UUID userUUID = Bukkit.getOfflinePlayer(playerTarget).getUniqueId();
        String permission = mvWorldManager.getMVWorld(worldName).getAccessPermission().getName();

        UserManager userManager = luckPerms.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(userUUID);

        User target = giveMeADamnUser(userUUID);
        if(!target.getCachedData().getPermissionData().checkPermission(permission).asBoolean()){
            return Format.chat("&e" +playerTarget+ " doesn't have access to this world!");
        }
        luckPerms.getUserManager().modifyUser(userUUID, user -> {
            user.data().remove(Node.builder(permission).build());
        });

        if(Bukkit.getOfflinePlayer(userUUID).isOnline()){
            checkPermissionWorld(Bukkit.getPlayer(userUUID));
            System.out.println("asd");
        }

        return Format.chat("&a" +playerTarget+ " now doesn't have access to this world!");
    }

    private String trustGroup(String groupTarget,String worldName){
        GroupManager groupManager = luckPerms.getGroupManager();
        String permission = mvWorldManager.getMVWorld(worldName).getAccessPermission().getName();

        if(groupManager.getGroup(groupTarget).getCachedData().getPermissionData().checkPermission(permission).asBoolean()){
            return Format.chat("&e" +groupTarget+ " already have access to this world!");
        }

        groupManager.modifyGroup(groupTarget,group -> {
            group.data().add(Node.builder(permission).build());
        });
        return Format.chat("&a" +groupTarget+ " now have access to this world!");
    }

    private String unTrustGroup(String groupTarget,String worldName){
        GroupManager groupManager = luckPerms.getGroupManager();
        String permission = mvWorldManager.getMVWorld(worldName).getAccessPermission().getName();

        if(!groupManager.getGroup(groupTarget).getCachedData().getPermissionData().checkPermission(permission).asBoolean()){
            return Format.chat("&e" +groupTarget+ " doesn't have access to this world!");
        }

        groupManager.modifyGroup(groupTarget,group -> {
            group.data().remove(Node.builder(permission).build());
        });
        return Format.chat("&a" +groupTarget+ " now doesn't have access to this world!");
    }

    public void checkPermissionWorld(Player target){
        FileConfiguration wmConfig = Config.getWorldConfig();
        String currentWorld = target.getWorld().getName();
        List<String> globalWorld = wmConfig.getStringList("globalWorld");
        if(globalWorld.contains(currentWorld)) return;

        User userPlayer = giveMeADamnUser(target.getUniqueId());
        String permission = mvWorldManager.getMVWorld(currentWorld).getAccessPermission().getName();
        if(userPlayer.getCachedData().getPermissionData().checkPermission(permission).asBoolean()) return;

        target.teleport(mvWorldManager.getMVWorld(wmConfig.getString("defaultWorld")).getSpawnLocation());
        target.sendMessage(Format.chat("&cYou are not allowed to enter " +currentWorld+ " world, you must be trusted to enter " +currentWorld+ " world"));
    }

    private User giveMeADamnUser(UUID uniqueId) {
        UserManager userManager = luckPerms.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(uniqueId);
        return userFuture.join();
    }
}
