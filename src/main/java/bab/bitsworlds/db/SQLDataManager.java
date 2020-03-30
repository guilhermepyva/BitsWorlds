package bab.bitsworlds.db;

import bab.bitsworlds.logger.Log;
import bab.bitsworlds.logger.LogAction;
import bab.bitsworlds.logger.LogRecorder;
import bab.bitsworlds.multilanguage.Lang;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLDataManager {
    public static void insertLog(Log log) throws SQLException {
        PreparedStatement statement = BWSQL.dbCon.prepareStatement("INSERT INTO log(action, data, recorder_type, recorder_uuid, note, note_appender_uuid, time, world, worldname) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        statement.setString(1, log.action.name());
        statement.setString(2, log.data == null ? null : log.data.toString());
        statement.setString(3, log.recorder.type.name());
        statement.setString(4, log.recorder.uuid != null ? log.recorder.uuid.toString() : null);
        statement.setString(5, log.note);
        statement.setString(6, log.noteRecorder != null ? log.noteRecorder.uuid.toString() : null);
        statement.setTimestamp(7, log.time);
        statement.setString(8, log.world != null ? log.world.toString() : null);
        statement.setString(9, log.worldName);

        statement.execute();

        statement.close();
    }

    public static void updateNoteLog(int id, String note, String note_appender) throws SQLException {
        Statement stm = BWSQL.dbCon.createStatement();

        stm.execute("UPDATE log SET note = '" + note + "', note_appender_uuid = '" + note_appender + "' WHERE id = " + id);
    }

    public static List<Log> queryLogs(String additional, String where) throws SQLException {
        Statement stm = BWSQL.dbCon.createStatement();

        ResultSet result = stm.executeQuery("SELECT * FROM log" + where +" ORDER BY id DESC" + additional);

        List<Log> list = new ArrayList<>();

        while (result.next()) {
            list.add(getLogFromResultSet(result));
        }

        result.close();
        stm.close();

        return list;
    }

    public static int queryCountLogs() throws SQLException {
        Statement stm = BWSQL.dbCon.createStatement();

        ResultSet result = stm.executeQuery("SELECT COUNT(*) FROM log");

        int count;

        result.next();

        count = result.getInt(1);

        result.close();
        stm.close();

        return count;
    }

    public static Log getLogFromResultSet(ResultSet resultSet) throws SQLException {
        LogAction action = LogAction.valueOf(resultSet.getString("action"));
        Object data = null;

        switch (action) {
            case GLOBAL_CONFIG_DATABASETYPESET:
                data = resultSet.getString("data").equals("sqlite");
                break;
            case GLOBAL_CONFIG_LANGUAGESET:
                data = Lang.valueOf(resultSet.getString("data"));
                break;
            case GLOBAL_CONFIG_NOTESSET:
                data = Boolean.valueOf(resultSet.getString("data"));
                break;
            case WORLD_CREATED:
                data =  resultSet.getString("data");
                break;
        }

        LogRecorder noteRecorder = null;

        if (resultSet.getString("note_appender_uuid") == null)
            noteRecorder = new LogRecorder(LogRecorder.RecorderType.SYSTEM);
        else
            noteRecorder = new LogRecorder(UUID.fromString(resultSet.getString("note_appender_uuid")));

        return new Log(
                resultSet.getInt("id"),
                action,
                data,
                new LogRecorder(LogRecorder.RecorderType.valueOf(resultSet.getString("recorder_type")), resultSet.getString("recorder_uuid") != null ? UUID.fromString(resultSet.getString("recorder_uuid")) : null),
                resultSet.getString("note"),
                noteRecorder,
                resultSet.getTimestamp("time"),
                resultSet.getString("world") != null ? UUID.fromString(resultSet.getString("world")) : null,
                resultSet.getString("worldName"));
    }
}