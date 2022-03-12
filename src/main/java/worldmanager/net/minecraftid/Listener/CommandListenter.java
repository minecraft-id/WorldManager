package worldmanager.net.minecraftid.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListenter implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage();
        if(cmd.contains("/mv list") || cmd.contains("/mvlist")){
            event.setCancelled(true);
            event.getPlayer().performCommand("worldmanager list");
        }
    }
}
