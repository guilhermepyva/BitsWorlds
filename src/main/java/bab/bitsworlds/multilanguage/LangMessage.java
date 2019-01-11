package bab.bitsworlds.multilanguage;

import java.util.Objects;

/**
 * This class will store a message and the lang of the message
 * @see MLMessage
 * @see Lang
 */
public class LangMessage {
    /**
     * The language of the message
     */
    public Lang lang;

    /**
     * The message that will be in the respective language
     */
    public String message;

    public LangMessage(Lang lang, String message) {
        this.lang = lang;
        this.message = message;
    }

    public boolean checkLang(LangMessage langMessage) {
        return langMessage.lang == this.lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LangMessage that = (LangMessage) o;
        return lang == that.lang &&
                message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lang, message);
    }
}