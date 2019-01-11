package bab.bitsworlds.multilanguage;

import java.util.HashMap;

/**
 * This class will store MLMessages of a class with their respective keys
 */
public class ConjuntMessages {
    private HashMap<String, MLMessage> messages;

    public ConjuntMessages() {
        messages = new HashMap<>();
    }

    public void addMessage(String key, MLMessage message) {
        messages.put(key, message);
    }

    public void removeMessage(String key) {
        messages.remove(key);
    }

    public MLMessage getMessage(String key) {
        return messages.get(key);
    }

    public boolean containsKey(String key) {
        return messages.containsKey(key);
    }

    public void appendLangMessage(String key, LangMessage langMessage) {
        messages.get(key).appendLangMessage(langMessage);
    }
}
