package bab.bitsworlds.logger;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.cmd.ConfigCmd;
import bab.bitsworlds.cmd.LogCmd;
import bab.bitsworlds.db.SQLDataManager;
import bab.bitsworlds.gui.GUICore;
import bab.bitsworlds.gui.GUIItem;
import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LogCore {
    public static boolean notes;

    public static void addLog(LogAction action, Object data, LogRecorder recorder, Timestamp time) {
        addLog(action, data, recorder, null, null, time, null, null);
    }

    public static void addLog(LogAction action, Object data, LogRecorder recorder, Timestamp time, UUID world, String worldName) {
        addLog(action, data, recorder, null, null, time, world, worldName);
    }

    public static void addLog(LogAction action, Object data, LogRecorder recorder, String note, LogRecorder noteRecorder, Timestamp time, UUID world, String worldName) {
        Bukkit.getScheduler().runTaskAsynchronously(BitsWorlds.plugin, () -> {
            try {
                SQLDataManager.insertLog(new Log(0, action, data, recorder, note, noteRecorder, time, world, worldName));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        //TODO MEXER NISSO QND OS LOGS DOS MUNDOS ESTIVEREM PRONTOS
        GUICore.updateGUI("global_logs");
    }

    public static GUIItem getItemFromLog(Log log) {
        String title = ChatColor.DARK_PURPLE + ChatColor.BOLD.toString();
        List<String> description = new ArrayList<>();

        switch (log.action) {
            case GLOBAL_CONFIG_LANGUAGESET:
                title = title + LangCore.getClassMessage(LogCore.class, "global-config-languageset-title").toString();
                description.add(ChatColor.GOLD + LangCore.getClassMessage(LogCore.class, "global-config-languageset-lore").setKey("%%lang", ChatColor.WHITE + Lang.valueOf(log.data.toString()).title).toString());

                break;
            case GLOBAL_CONFIG_DATABASETYPESET:
                title = title + LangCore.getClassMessage(LogCore.class, "global-config-databasetype-set-title").toString();
                description.add(ChatColor.GOLD + LangCore.getClassMessage(LogCore.class, "global-config-databasetype-set-lore").setKey("%%db", ChatColor.WHITE + ((Boolean) log.data ? "SQLite" : "MySQL")).toString());

                break;
            case WORLD_CREATED:
                title = title + LangCore.getClassMessage(LogCore.class, "world-created-title").toString();
                description.add(ChatColor.GOLD + LangCore.getClassMessage(LogCore.class, "world-created-lore").setKey("%%s", ChatColor.WHITE + log.worldName + ChatColor.GOLD).toString());
        }

        String recorder = null;

        switch (log.recorder.type) {
            case PLAYER:
                recorder = Bukkit.getOfflinePlayer(log.recorder.uuid).getName();
                break;
            case SYSTEM:
                recorder = LangCore.getClassMessage(LogCore.class, "system-word").toString();
                break;
            case CONSOLE:
                recorder = LangCore.getClassMessage(LogCore.class, "console-word").toString();
                break;
        }

        String noteRecorder = null;

        switch (log.noteRecorder.type) {
            case PLAYER:
                noteRecorder = ChatColor.GOLD.toString() + ChatColor.ITALIC + "[" + Bukkit.getOfflinePlayer(log.noteRecorder.uuid).getName() + "]";
                break;
            case SYSTEM:
                noteRecorder = ChatColor.AQUA.toString() + ChatColor.ITALIC + "[" + LangCore.getClassMessage(LogCore.class, "system-word").toString() + "]";
                break;
        }

        description.add("");
        description.add(ChatColor.GOLD + LangCore.getClassMessage(LogCore.class, "recorder-word").setKey("%%r", ChatColor.WHITE + recorder).toString());
        description.add(ChatColor.GOLD + "ID: " + ChatColor.WHITE + log.id);
        description.add(ChatColor.GOLD + LangCore.getClassMessage(LogCore.class, "date-word").toString() + ChatColor.WHITE + LangCore.getDateByPattern(log.time.toLocalDateTime()));

        if (log.world != null)
            description.add(ChatColor.GOLD + LangCore.getClassMessage(LogCore.class, "world-word").setKey("%%w", ChatColor.WHITE + log.worldName + " (" + log.world + ")").toString());

        if (log.note != null)
            description.add(ChatColor.GOLD + LangCore.getClassMessage(LogCore.class, "note-word").setKey("%%n", ChatColor.WHITE + log.note).setKey("%%p", noteRecorder).toString());

        GUIItem item = new GUIItem(
                log.action.material,
                title,
                description
        );

        if (log.action == LogAction.GLOBAL_CONFIG_LANGUAGESET) {
            ConfigCmd.setCountryBanner(Lang.valueOf(log.data.toString()), item);
        }

        return item;
    }
}