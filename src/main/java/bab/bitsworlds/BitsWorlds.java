package bab.bitsworlds;

import bab.bitsworlds.cmd.BitsWorldsCmd;
import bab.bitsworlds.cmd.ConfigCmd;
import bab.bitsworlds.config.BWConfig;
import bab.bitsworlds.gui.GUIHandler;
import bab.bitsworlds.gui.ImplGUI;
import bab.bitsworlds.multilanguage.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

public class BitsWorlds extends JavaPlugin {

    /**
     * The current language of the plugin
     */
    public static Lang lang;
    public static Logger logger;

    @Override
    public void onEnable() {
        logger = Logger.getLogger("BitsWorlds");

        logger.info("Enabling BitsWorlds V" + this.getDescription().getVersion());
        loadConfigs();

        if (LangCore.load()) {
            throw new RuntimeException("[BitsWorlds] Couldn't load the Translation Files, report this for a Developer");
        }

        GUIHandler.init();

        loadListeners();
        loadCmd();
        loadPrefixes();
        logger.info("BitsWorlds V" + this.getDescription().getVersion() + " enabled successfully");
    }

    @Override
    public void onDisable() {

    }

    private void loadConfigs() {
        saveDefaultConfig();


        getConfig().getKeys(false).forEach( key -> BWConfig.loadConfig(key, getConfig().get(key)) );
    }

    private void loadPrefixes() {
        PrefixMessage.error = new PrefixMessage.Status(PrefixMessage.getPrefix(ChatColor.RED, ChatColor.DARK_RED, ChatColor.RED), ChatColor.RED);
        PrefixMessage.warn = new PrefixMessage.Status(PrefixMessage.getPrefix(ChatColor.YELLOW, ChatColor.GOLD, ChatColor.YELLOW), ChatColor.YELLOW);
        PrefixMessage.info = new PrefixMessage.Status(PrefixMessage.prefix, ChatColor.GREEN);
        PrefixMessage.permission_message =
                        PrefixMessage.error.getPrefix() +
                        PrefixMessage.error.getDefaultChatColor() +
                        LangCore.getClassMessage(getClass(), "permission_message").getTranslatedMessage().message;
    }

    private void loadCmd() {
        if (lang != Lang.EN) {
            getCommand("BitsWorlds").setDescription(
                    LangCore.getClassMessage(getClass(), "cmd_description").getTranslatedMessage().message);
            getCommand("BitsWorlds").setUsage(
                    LangCore.getClassMessage(getClass(), "cmd_usage").setKey("%%cmd", "/BitsWorlds").getTranslatedMessage().message);
        }

        getCommand("BitsWorlds").setPermissionMessage(PrefixMessage.permission_message);
        getCommand("BitsWorlds").setExecutor(new BitsWorldsCmd());
    }

    private void loadListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new GUIHandler(), this);

        GUIHandler.listeners.add(new ConfigCmd());
    }
}
