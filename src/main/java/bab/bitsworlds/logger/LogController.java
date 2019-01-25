package bab.bitsworlds.logger;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.db.SQLDataManager;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class LogController {
    public static void addLog(LogAction action, LogRecorder recorder, Timestamp time) {
        addLog(action, null, recorder, null, time, null);
    }

    public static void addLog(LogAction action, Object data, LogRecorder recorder, Timestamp time) {
        addLog(action, data, recorder, null, time, null);
    }

    public static void addLog(LogAction action, Object data, LogRecorder recorder, String description, Timestamp time, UUID world) {
        Bukkit.getScheduler().runTaskAsynchronously(BitsWorlds.plugin, () -> {
            try {
                SQLDataManager.insertLog(new Log(action, data, recorder, description, time, world));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}