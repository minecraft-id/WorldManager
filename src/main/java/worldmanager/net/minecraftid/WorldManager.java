package worldmanager.net.minecraftid;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.plugin.java.JavaPlugin;
import worldmanager.net.minecraftid.Commands.WorldManagerCommand;
import worldmanager.net.minecraftid.Listener.CommandListenter;
import worldmanager.net.minecraftid.Listener.WorldChangeListener;
import worldmanager.net.minecraftid.Utils.Config;

public final class WorldManager extends JavaPlugin {

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig());
    }

    @Override
    public void onEnable() {
        initConfig();
        new WorldManagerCommand().initCommand();
        CommandAPI.onEnable(this);
        getServer().getPluginManager().registerEvents(new CommandListenter(),this);
        getServer().getPluginManager().registerEvents(new WorldChangeListener(), this);
    }

    private void initConfig(){
        new Config().init();
    }
}
