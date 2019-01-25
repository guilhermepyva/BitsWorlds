package bab.bitsworlds.db;

import bab.bitsworlds.BitsWorlds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BWSQL {
    public static boolean sqlite;

    //DB Logins
    public static String host;
    public static int port = 0;
    public static String databaseName;
    public static String user = "";
    public static String pw = "";

    public static Connection dbCon;

    public static void connect() {
        try {
            if (sqlite) {
                Class.forName("org.sqlite.JDBC");

                dbCon = DriverManager.getConnection("jdbc:sqlite:" + BitsWorlds.plugin.getDataFolder().getAbsolutePath() + "/bwdb.db");
            }
            else {
                if (host == null || databaseName == null) {
                    throw new NullPointerException("Invalid MySQL credentials in config.yml");
                }

                Class.forName("com.mysql.jdbc.Driver");

                dbCon = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName, user, pw);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        if (dbCon != null) {
            try {
                dbCon.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setupDB() {
        try {
            Statement statement = dbCon.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS log" +
                    "(" +
                    "    action VARCHAR(30)," +
                    "    data VARCHAR(200)," +
                    "    recorder_type VARCHAR(30)," +
                    "    recorder_uuid CHARACTER(36)," +
                    "    description VARCHAR(200)," +
                    "    time TIMESTAMP default current_timestamp, " +
                    "    world CHARACTER(36)," +
                    "    worldName VARCHAR(200)" +
                    ")" + (BWSQL.sqlite ? ";" : " ENGINE = INNODB;"));

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
