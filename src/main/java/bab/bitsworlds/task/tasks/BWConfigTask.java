package bab.bitsworlds.task.tasks;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import bab.bitsworlds.task.BWTask;
import bab.bitsworlds.task.BWTaskResponse;
import bab.bitsworlds.task.responses.DefaultResponse;

public class BWConfigTask extends BWTask {

    public ConfigTask task;
    public Object data;

    public BWConfigTask(ConfigTask task, Lang lang) {
        this.task = task;
        this.data = lang;
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

                PrefixMessage.permission_message =
                        PrefixMessage.error.getPrefix() +
                                PrefixMessage.error.getDefaultChatColor() +
                                LangCore.getClassMessage(BitsWorlds.class, "permission_message").getTranslatedMessage().message;

                return new DefaultResponse(2);
        }

        return null;
    }

    public enum ConfigTask {
        LanguageSet
    }
}