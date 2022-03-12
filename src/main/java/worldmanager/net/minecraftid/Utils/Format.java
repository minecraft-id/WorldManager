package worldmanager.net.minecraftid.Utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Format{

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static String serverName;

    public static void setServerName(String string){
        serverName = "&r"+string;
    }
    public static String getServerName(){
        return serverName;
    }

    private static String translateHexColorCodes(final String message) {
        final char colorChar = ChatColor.COLOR_CHAR;
        final Matcher matcher = HEX_PATTERN.matcher(message);
        final StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);
            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }
        return matcher.appendTail(buffer).toString();
    }

    public static String chat(String message){
        return ChatColor.translateAlternateColorCodes('&',translateHexColorCodes(serverName+message));
    }

    public static String argumentEx(String message){
        return ChatColor.stripColor(chat(message));
    }

    public static String tellraw(String[] text,String command,String placeholder){
        String hover = "\"clickEvent\":{\"action\":\"run_command\",\"value\":\""+command+"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Will teleport you to the \"}]}}";
        String tellraw = "{\"text\":\" "+text[0]+" \","+hover+"},{\"text\":\"("+text[1]+")\\n\",\"color\":\"green\","+hover+"}";
        return tellraw;
    }

}
