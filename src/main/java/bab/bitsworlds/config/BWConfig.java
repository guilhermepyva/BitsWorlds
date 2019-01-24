package bab.bitsworlds.config;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.db.BWSQL;
import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class BWConfig {

    /**
     * This is a Config Loader that will read each key and load them in their respective systems
     * @param key the key of the config
     * @param value the value of the config
     */

    public static void loadConfig(String key, Object value) {
        FileConfiguration config = BitsWorlds.plugin.getConfig();

        switch(key) {
            case "language":
                LangCore.lang = Lang.valueOf(value.toString());

                break;

            case "db":
                BWSQL.sqlite = value.toString().equalsIgnoreCase("sqlite");

                ConfigurationSection mysqlCredentials = config.getConfigurationSection("mysql-credentials");

                if (!BWSQL.sqlite) {
                    BWSQL.host = mysqlCredentials.getString("host");
                    BWSQL.port = mysqlCredentials.getInt("port");
                    BWSQL.databaseName = mysqlCredentials.getString("database");
                    BWSQL.user = mysqlCredentials.getString("user");
                    BWSQL.pw = mysqlCredentials.getString("password");
                }

                break;
        }
    }
}
