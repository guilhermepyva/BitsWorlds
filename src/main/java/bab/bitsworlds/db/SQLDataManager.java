package bab.bitsworlds.db;

import bab.bitsworlds.logger.Log;
import bab.bitsworlds.logger.LogAction;
import bab.bitsworlds.logger.LogRecorder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLDataManager {
    public static void insertLog(Log log) throws SQLException {
        PreparedStatement statement = BWSQL.dbCon.prepareStatement("INSERT INTO log VALUES (?, ?, ?, ?, ?, ?)");

        statement.setString(1, log.action.name());
        statement.setString(2, log.recorder.type.name());
        statement.setString(3, log.recorder.uuid != null ? log.recorder.uuid.toString() : null);
        statement.setString(4, log.description);
        statement.setString(5, log.time.toString());
        statement.setString(6, log.world != null ? log.world.toString() : null);

        statement.execute();

        statement.close();
    }

    public static List<Log> queryAllLogs() throws SQLException {
        Statement stm = BWSQL.dbCon.createStatement();

        ResultSet result = stm.executeQuery("SELECT * FROM logs");

        List<Log> list = new ArrayList<>();

        while (result.next()) {
            list.add(getLogFromResultSet(result));
        }

        stm.close();

        return list;
    }

    public static Log getLogFromResultSet(ResultSet resultSet) throws SQLException {
        return new Log(LogAction.valueOf(resultSet.getString("action")),
                new LogRecorder(LogRecorder.RecorderType.valueOf(resultSet.getString("recorder_type")), UUID.fromString(resultSet.getString("recorder_uuid"))),
                resultSet.getString("description"),
                resultSet.getTimestamp("time"),
                UUID.fromString(resultSet.getString("world")));
    }
}