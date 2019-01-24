package bab.bitsworlds.db;

import bab.bitsworlds.logger.Log;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLDataManager {
    public static void insertLog(Log log) throws SQLException {
        PreparedStatement statement = BWSQL.dbCon.prepareStatement("INSERT INTO log VALUES (?, ?, ?, ?, ?)");

        statement.setString(1, log.action.name());
        statement.setString(2, log.recorder.type.name());
        statement.setString(3, log.recorder.uuid != null ? log.recorder.uuid.toString() : null);
        statement.setString(4, log.description);
        statement.setString(5, String.valueOf(log.time.getTime()));

        statement.execute();

        statement.close();
    }


}