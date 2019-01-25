package bab.bitsworlds.logger;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.cmd.LogCmd;
import bab.bitsworlds.db.SQLDataManager;
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
    public static void addLog(LogAction action, LogRecorder recorder, Timestamp time) {
        addLog(action, null, recorder, null, time, null, null);
    }

    public static void addLog(LogAction action, Object data, LogRecorder recorder, Timestamp time) {
        addLog(action, data, recorder, null, time, null, null);
    }

    public static void addLog(LogAction action, Object data, LogRecorder recorder, String description, Timestamp time, UUID world, String worldName) {
        Bukkit.getScheduler().runTaskAsynchronously(BitsWorlds.plugin, () -> {
            try {
                SQLDataManager.insertLog(new Log(action, data, recorder, description, time, world, worldName));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static GUIItem getItemFromLog(Log log) {
        String title = null;
        List<String> description = new ArrayList<>();

        switch (log.action) {
            case GLOBAL_CONFIG_LANGUAGESET:
                title = ChatColor.AQUA + "" + ChatColor.BOLD + LangCore.getClassMessage(LogCore.class, "global-config-languageset-title").toString();
                description.add(ChatColor.AQUA + LangCore.getClassMessage(LogCore.class, "global-config-languageset-lore").setKey("%%lang", ChatColor.WHITE + Lang.valueOf(log.data.toString()).name()).toString());

                break;
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

        description.add("");
        description.add(ChatColor.AQUA + LangCore.getClassMessage(LogCore.class, "recorder-word").setKey("%%r", ChatColor.WHITE + recorder).toString());
        description.add(ChatColor.AQUA + LangCore.getDateByPattern(log.time.toLocalDateTime(), ChatColor.WHITE));
        if (log.world != null)
            description.add(ChatColor.AQUA + LangCore.getClassMessage(LogCore.class, "world-word").setKey(ChatColor.WHITE + "%%w", log.worldName + " (" + log.world + ")").toString());

        return new GUIItem(
                log.action.material,
                title,
                description
        );
    }
}