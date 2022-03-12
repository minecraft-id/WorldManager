package worldmanager.net.minecraftid.Listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginLogger;
import worldmanager.net.minecraftid.Commands.WorldManagerCommand;

public class WorldChangeListener implements Listener {

    WorldManagerCommand wmCommand = new WorldManagerCommand();

    @EventHandler
    public void playerOnJoin(PlayerJoinEvent event){
        try {
            wmCommand.checkPermissionWorld(event.getPlayer());
        }catch (Exception e){
            Bukkit.getLogger().info(e.getMessage());
        }
    }

    @EventHandler
    public void playerTeleportWorld(PlayerChangedWorldEvent event){
        try {
            wmCommand.checkPermissionWorld(event.getPlayer());
        }catch (Exception e){
            Bukkit.getLogger().info(e.getMessage());
        }
    }

}
