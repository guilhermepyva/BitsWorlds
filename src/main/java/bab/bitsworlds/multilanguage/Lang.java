package bab.bitsworlds.multilanguage;

/**
 * Enum that store the plugin supported languages
 * @see LangMessage
 * @see MLMessage
 */
public enum Lang {
    EN("English"),
    PT("Português"),
    SP("Español"),
    FR("Français");

    public String title;
    Lang(String title) {
        this.title = title;
    }
}
