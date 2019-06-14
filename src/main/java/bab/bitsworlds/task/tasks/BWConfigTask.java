package bab.bitsworlds.task.tasks;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.logger.LogAction;
import bab.bitsworlds.logger.LogCore;
import bab.bitsworlds.logger.LogRecorder;
import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.task.BWTask;
import bab.bitsworlds.task.BWTaskResponse;
import bab.bitsworlds.task.responses.DefaultResponse;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Timestamp;
import java.util.UUID;

public class BWConfigTask extends BWTask {

    private ConfigTask task;
    private Object data;
    private UUID player;

    public BWConfigTask(ConfigTask task, Object data, UUID player) {
        this.task = task;
        this.data = data;
        this.player = player;
    }

    @Override
    protected BWTaskResponse run() {
        switch (task) {
            case LanguageSet:
                Lang lang = (Lang) data;

                if (LangCore.lang == lang) {
                    return new DefaultResponse(1);
                }

                LangCore.lang = lang;
                BitsWorlds.plugin.getConfig().set("language", LangCore.lang.name());
                BitsWorlds.plugin.saveConfig();

                LogCore.addLog(LogAction.GLOBAL_CONFIG_LANGUAGESET, LangCore.lang.name(), new LogRecorder(player), new Timestamp(System.currentTimeMillis()));

                return new DefaultResponse(2);
            case DatabaseTypeSet:
                boolean sqlite = (boolean) data;
                FileConfiguration config = BitsWorlds.plugin.getConfig();

                if ((sqlite && config.getString("db").equalsIgnoreCase("sqlite")) || !sqlite && config.getString("db").equalsIgnoreCase("mysql")) {
                    return new DefaultResponse(1);
                }

                String type = sqlite ? "sqlite" : "mysql";

                config.set("db", type);
                BitsWorlds.plugin.saveConfig();

                LogCore.addLog(LogAction.GLOBAL_CONFIG_DATABASETYPESET, type, new LogRecorder(player), new Timestamp(System.currentTimeMillis()));

                return new DefaultResponse(2);
        }

        return null;
    }

    public enum ConfigTask {
        LanguageSet,
        DatabaseTypeSet
    }
}