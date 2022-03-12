package worldmanager.net.minecraftid.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class Config {
    private static String folderPath = "./plugins/MinecraftID";
    private static File publicConfigFile, coreFolder, worldConfigFile, wmConfigFile;
    private static FileConfiguration publicConfig, worldConfig, wmConfig;

    public void init(){
        coreFolder = Paths.get(folderPath).toFile();
        setup();
    }

    private void setup(){
        List<File> configFiles = new ArrayList<>();
        publicConfigFile = new File(coreFolder, "config.yml");
        configFiles.add(publicConfigFile);

        worldConfigFile = new File(coreFolder, "WorldManager/world.yml");
        configFiles.add(worldConfigFile);

        wmConfigFile = new File(coreFolder, "WorldManager/config.yml");
        configFiles.add(wmConfigFile);

        for (File file : configFiles) {
            String[] folderName = file.getParentFile().toString().replace("\\","/").split(folderPath);
            String Folder = "/";
            if(folderName.length != 0){
                Folder = folderName[folderName.length - 1]+"/";
            }
            if(!file.exists()){
                file.getParentFile().mkdir();
                try (InputStream input = getClass().getResourceAsStream(Folder + file.getName())) {
                    if (input != null) {
                        Files.copy(input, file.toPath());
                    } else {
                        file.createNewFile();
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }


        publicConfig = YamlConfiguration.loadConfiguration(publicConfigFile);
        worldConfig =  YamlConfiguration.loadConfiguration(worldConfigFile);
        wmConfig = YamlConfiguration.loadConfiguration(wmConfigFile);
        save();
    }

    public static PluginDescriptionFile getPluginDes(){
        return Bukkit.getPluginManager().getPlugin("WorldManager").getDescription();
    }
    public static FileConfiguration getPublicConfig(){
        return publicConfig;
    }
    public static FileConfiguration getWmConfig(){
        return wmConfig;
    }
    public static FileConfiguration getWorldConfig(){
        return worldConfig;
    }

    public void setWorldManagerConfig(FileConfiguration worldManagerConfig){
        this.worldConfig = worldManagerConfig;
    }
    public void setWmConfig(FileConfiguration wmConfig){
        this.wmConfig = wmConfig;
    }

    public boolean save(){
        try {
            publicConfig.save(publicConfigFile);
            worldConfig.save(worldConfigFile);
            wmConfig.save(wmConfigFile);
            reload();
            return true;
        }catch (IOException e){
            getLogger().info(e.getMessage());
            return false;
        }
    }

    public static void reload(){
        publicConfig = YamlConfiguration.loadConfiguration(publicConfigFile);
        worldConfig =  YamlConfiguration.loadConfiguration(worldConfigFile);
        wmConfig = YamlConfiguration.loadConfiguration(wmConfigFile);
        if(wmConfig.getBoolean("useGlobalServerName")){
            Format.setServerName(publicConfig.getString("serverName"));
            return;
        }
        Format.setServerName(wmConfig.getString("pluginName"));
    }
}
