package bab.bitsworlds.multilanguage;

import org.bukkit.ChatColor;

/**
 * The class that contain the plugin message patterns
 */
public class PrefixMessage {

    /**
     * The Status is used to store a message pattern for a status type
     */
    public static class Status {
        private String prefix;
        private ChatColor defaultChatColor;

        public String getPrefix() {
            return prefix;
        }

        public ChatColor getDefaultChatColor() {
            return defaultChatColor;
        }

        public Status(String prefix, ChatColor defaultChatColor) {
            this.prefix = prefix;
            this.defaultChatColor = defaultChatColor;
        }
    }

    /**
     * Create the same prefix with different colors
     * @param color1 "BitsWorlds" color and message color
     * @param color2 Bracket colors
     * @return the prefix with the defined colors
     */
    public static String getPrefix(ChatColor color1, ChatColor color2, ChatColor messageColor) {
        return color2 + "" + ChatColor.BOLD + "[" + color1 + "" + ChatColor.BOLD + "BitsWorlds" + color2 + "" + ChatColor.BOLD + "] " + messageColor;
    }

    /**
     * The plugin messages prefix
     */
    public static String prefix = getPrefix(ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.GREEN);

    /**
     * The plugin error Status
     */
    public static Status error;
    /**
     * The plugin warn Status
     */
    public static Status warn;
    /**
     * The plugin info Status
     */
    public static Status info;

    /**
     * The permission error message
     */
    public static String permission_message;
}
