package bab.bitsworlds.multilanguage;

import bab.bitsworlds.BitsWorlds;

import java.util.*;

//Multi-Language Message

/**
 * This class will store {@link LangMessage}'s to be sent in the correct language
 * @see LangMessage
 * @see Lang
 */
public class MLMessage {
    private List<LangMessage> messages;
    private Map<String, String> keys;

    /**
     * Return the messages of this Multi-Language Message
     * @return a clone of {@link MLMessage#messages}
     */
    public LangMessage[] getLangMessages() {
        return (LangMessage[]) messages.toArray().clone();
    }

    /**
     * Default constructor
     * @param messages the {@link LangMessage}'s to storage in this Multi-Language Message
     */
    public MLMessage(LangMessage... messages) {
        keys = new HashMap<>();

        boolean anyMatch = Arrays.stream(messages).anyMatch(langMessage -> {
            for (LangMessage message : messages) {
                if (langMessage.checkLang(message) && !langMessage.equals(message)) {
                    return true;
                }
            }
            return false;
        });

        if (anyMatch)
            throw new NullPointerException("An MLMessage can't have more than two Lang Messages of the same language");

        this.messages = new ArrayList<>(Arrays.asList(messages));
    }


    /**
     * Append a {@link LangMessage} to this MLMessage
     * @throws NullPointerException if already has a LangMessage
     * inside within this MLMessage that is in the same Lang as the LangMessage that is trying to be inserted
     * @param langMessage that will be appended
     */
    public void appendLangMessage(LangMessage langMessage) {
        boolean anyMatch = messages.stream().anyMatch( message -> message.checkLang(langMessage));

        if (anyMatch)
            throw new RuntimeException("An MLMessage can't have more than two Lang Messages of the same language");

        messages.add(langMessage);
    }

    /**
     * Return the LangMessage stored in this Multi-Language Message
     * @param lang the message language to get in this Multi-Language Message
     * @return the {@link LangMessage} of this Multi-Language Message,
     * if the message in that {@link Lang} don't exists here, then will return {@code null}
     */
    public LangMessage getLangMessage(Lang lang) {
        Optional<LangMessage> oplangMessage = replaceKeys().stream().filter(langMessage -> langMessage.lang == lang).findFirst();

        if (oplangMessage.isPresent())
            return oplangMessage.get();

        return null;
    }

    /**
     * This will set the value of a key in the {@link LangMessage}'s
     * <p>
     *     The keys in a LangMessage mean parts of the phrase that don't need translation
     *     and will be the same for each of the {@link LangMessage}'s
     * </p>
     * <p>A key is usually recognized by a letter or word after two % (percentage), example: %%link</p>
     * @param key The key
     * @param value The value that the key will assume in each of the {@link LangMessage}'s
     */
    public MLMessage setKey(String key, String value) {
        keys.put(key, value);
        return this;
    }

    /**
     * Shortcut to {@link MLMessage#setKey(String, String)} with MLMessages
     * @return
     */
    public MLMessage setKey(String key, MLMessage message) {
        return setKey(key, message.getTranslatedMessage().message);
    }

    public List<LangMessage> replaceKeys() {
        List<LangMessage> langMessages = new ArrayList<>();

        /*keys.keySet().forEach(key -> {
            messages.forEach(message -> {
                LangMessage newLangMessage = null;
                try {
                    newLangMessage = message.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                newLangMessage.message = newLangMessage.message.replace(key, keys.get(key));

                langMessages.add(newLangMessage);
            });
        });*/

        messages.forEach(message -> {
            LangMessage newLangMessage = null;

            try {
                newLangMessage = message.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            for (String key : keys.keySet()) {
                newLangMessage.message = newLangMessage.message.replace(key, keys.get(key));
            }

            langMessages.add(newLangMessage);
        });

        return langMessages;
    }

    /**
     * Util to get the translation of a message based on the server's current language
     * <p>You don't need to use it in {@link bab.bitsworlds.extensions.BWCommandSender#sendMessage(MLMessage)} because this operation
     *  is already applied in the method</p>
     * @throws NullPointerException if the message don't contains any {@link LangMessage}
     * @return the {@link LangMessage} of corresponding to the server language
     */
    public LangMessage getTranslatedMessage() {
        //Try to get the server language first
        List<LangMessage> listReplacedKeys = replaceKeys();
        Optional<LangMessage> oplangMessage =  listReplacedKeys.stream().filter(langMessage -> langMessage.lang == LangCore.lang).findFirst();

        if (oplangMessage.isPresent())
            return oplangMessage.get();

        //If goes wrong, try to get EN language
        if (LangCore.lang != Lang.EN) {
            LangMessage enLangMessage = getLangMessage(Lang.EN);

            if (enLangMessage != null)
                return enLangMessage;
        }

        //If goes wrong, try to get any language

        Optional<LangMessage> oplangMessage1 = listReplacedKeys.stream().filter(langMessage -> langMessage.lang != Lang.EN || langMessage.lang != LangCore.lang).findFirst();

        if (oplangMessage1.isPresent())
            return oplangMessage1.get();

        throw new NullPointerException("Message don't contains any LangMessage");
    }
}