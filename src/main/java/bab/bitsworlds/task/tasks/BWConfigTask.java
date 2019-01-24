package bab.bitsworlds.task.tasks;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.logger.LogAction;
import bab.bitsworlds.logger.LogController;
import bab.bitsworlds.logger.LogRecorder;
import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import bab.bitsworlds.task.BWTask;
import bab.bitsworlds.task.BWTaskResponse;
import bab.bitsworlds.task.responses.DefaultResponse;

import java.sql.Timestamp;
import java.util.UUID;

public class BWConfigTask extends BWTask {

    private ConfigTask task;
    private Object data;
    private UUID player;

    public BWConfigTask(ConfigTask task, Lang lang, UUID player) {
        this.task = task;
        this.data = lang;
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

                LogController.addLog(LogAction.GLOBAL_CONFIG_LANGUAGESET, new LogRecorder(player), new Timestamp(System.currentTimeMillis()));

                return new DefaultResponse(2);
        }

        return null;
    }

    public enum ConfigTask {
        LanguageSet
    }
}