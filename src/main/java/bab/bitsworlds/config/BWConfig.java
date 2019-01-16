package bab.bitsworlds.config;

import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangCore;

public class BWConfig {

    /**
     * This is a Config Loader that will read each key and load them in their respective systems
     * @param key the key of the config
     * @param value the value of the config
     */
    public static void loadConfig(String key, Object value) {
        switch(key) {
            case "language":
                LangCore.lang = Lang.valueOf(value.toString());

                break;
        }
    }
}
