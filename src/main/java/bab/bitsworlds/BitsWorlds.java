package bab.bitsworlds;

import bab.bitsworlds.cmd.BitsWorldsCmd;
import bab.bitsworlds.config.BWConfig;
import bab.bitsworlds.db.BWSQL;
import bab.bitsworlds.gui.GUICore;
import bab.bitsworlds.multilanguage.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

public class BitsWorlds extends JavaPlugin {

    public static Logger logger;

    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        logger = Logger.getLogger("BitsWorlds");

        logger.info("Enabling BitsWorlds V" + this.getDescription().getVersion());

        loadConfigs();

        //getConfig().addDefault();

        BWSQL.connect();

        if (LangCore.load()) {
            throw new RuntimeException("[BitsWorlds] Couldn't load the Translation Files, report this for a Developer");
        }

        loadListeners();
        loadCmd();
        loadPrefixes();
        logger.info("BitsWorlds V" + this.getDescription().getVersion() + " enabled successfully");
    }

    @Override
    public void onDisable() {
        BWSQL.disconnect();
    }

    private void loadConfigs() {
        getConfig().getKeys(false).forEach( key -> BWConfig.loadConfig(key, getConfig().get(key)) );
    }

    private void loadPrefixes() {
        PrefixMessage.error = new PrefixMessage.Status(PrefixMessage.getPrefix(ChatColor.RED, ChatColor.DARK_RED, ChatColor.RED), ChatColor.RED);
        PrefixMessage.warn = new PrefixMessage.Status(PrefixMessage.getPrefix(ChatColor.YELLOW, ChatColor.GOLD, ChatColor.YELLOW), ChatColor.YELLOW);
        PrefixMessage.info = new PrefixMessage.Status(PrefixMessage.prefix, ChatColor.GREEN);
        PrefixMessage.permission_message =
                PrefixMessage.error.getPrefix() +
                        PrefixMessage.error.getDefaultChatColor() +
                        LangCore.getClassMessage(getClass(), "permission_message").toString();
    }

    private void loadCmd() {
        if (LangCore.lang != Lang.EN) {
            getCommand("BitsWorlds").setDescription(
                    LangCore.getClassMessage(getClass(), "cmd_description").toString());
            getCommand("BitsWorlds").setUsage(
                    LangCore.getClassMessage(getClass(), "cmd_usage").setKey("%%cmd", "/BitsWorlds").toString());
        }

        getCommand("BitsWorlds").setPermissionMessage(PrefixMessage.permission_message);
        getCommand("BitsWorlds").setExecutor(new BitsWorldsCmd());
    }

    private void loadListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new GUICore(), this);
    }
}
