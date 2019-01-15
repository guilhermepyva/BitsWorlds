package bab.bitsworlds.task.tasks;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.task.BWTask;
import bab.bitsworlds.task.BWTaskResponse;
import bab.bitsworlds.task.responses.DefaultResponse;

public class BWConfigTask extends BWTask {

    public ConfigTask task;
    public Object data;

    public BWConfigTask() {
        this.task = task;
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

                return new DefaultResponse(2);
        }

        return null;
    }

    public enum ConfigTask {
        LanguageSet
    }
}