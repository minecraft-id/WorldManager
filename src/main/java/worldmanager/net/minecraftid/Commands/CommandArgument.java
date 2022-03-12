package worldmanager.net.minecraftid.Commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import worldmanager.net.minecraftid.Utils.Config;
import worldmanager.net.minecraftid.Utils.Format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandArgument{

    public static Argument worldManager(String nodeName){
        if(nodeName.equals("globalWorld")) {
            return new CustomArgument<String>(nodeName, info -> {
                FileConfiguration wmConfig = Config.getWorldConfig();
                List<String> listWorld = wmConfig.getStringList("globalWorld");
                if(!listWorld.contains(info.input())) {
                    throw new CustomArgument.CustomArgumentException(Format.argumentEx("World "+info.input()+" is not a global world.")) ;
                } else {
                    return info.input();
                }
            }).replaceSuggestions(sender ->{
                FileConfiguration wmConfig = Config.getWorldConfig();
                List<String> listWorld = wmConfig.getStringList("globalWorld");
                return listWorld.toArray(String[]::new);
            });
        }
        return new CustomArgument<String>(nodeName, info -> {
            World world = Bukkit.getWorld(info.input());
            if(world == null) {
                throw new CustomArgument.CustomArgumentException(Format.argumentEx("World "+info.input()+" is not exist."));
            } else {
                return world.getName();
            }
        }).replaceSuggestions(sender -> {
            return Bukkit.getWorlds().stream().map(World::getName).toArray(String[]::new);
        });
    }

    public static Argument player(String nodeName){
        return new CustomArgument<String>(nodeName, info ->{
            List<String> username = getPlayers();
            String usernameInput = info.input();
            if(!username.contains(usernameInput)) {
                throw new CustomArgument.CustomArgumentException(Format.argumentEx("Players with nickname "+info.input()+" is not exist from this server.")) ;
            } else {
                return usernameInput;
            }
        }).replaceSuggestions(sender ->{
            List<String> names = getPlayers();
            return names.toArray(String[]::new);
        });
    }

    public static Argument luckPermsGroup(String nodeName){
        return new CustomArgument<String>(nodeName, info ->{
            List<String> groups = new ArrayList<String>();
            new WorldManagerCommand().luckPerms.getGroupManager().getLoadedGroups().forEach(group -> {
                groups.add(group.getName());
            });
            String groupName = info.input();
            if(!groups.contains(groupName)) {
                throw new CustomArgument.CustomArgumentException(Format.argumentEx("Group with name "+info.input()+" is not exist.")) ;
            } else {
                return groupName;
            }
        }).replaceSuggestions(sender ->{
            List<String> groups = new ArrayList<String>();
            new WorldManagerCommand().luckPerms.getGroupManager().getLoadedGroups().forEach(group -> {
                groups.add(group.getName());
            });
            return groups.toArray(String[]::new);
        });
    }

    private static List<String> getPlayers(){
        OfflinePlayer[] offlinePlayer = Bukkit.getServer().getOfflinePlayers();
        Collection<? extends Player> onlinePlayer = Bukkit.getServer().getOnlinePlayers();
        List<String> username = new ArrayList<String>();
        for(OfflinePlayer player : offlinePlayer){
            username.add(player.getName());
        }
        for(Player player : onlinePlayer){
            username.add(player.getName());
        }
        return username;
    }
}
