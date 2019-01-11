package bab.bitsworlds.multilanguage;

import bab.bitsworlds.BitsWorlds;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;

public class LangCore {

    /**
     * The current language of the plugin
     */
    public static Lang lang;

    /**
     * Store all messages in the server Language
     */
    public static Map<String, ConjuntMessages> messages;

    /**
     * Store all util messages
     * These messages will be used in multiple classes and several times
     */
    public static ConjuntMessages utilMessages;

    /**
     * This will init the loader of the Translation Files
     * and store in {@link LangCore#messages}
     */
    public static boolean load() {
        messages = new HashMap<>();
        utilMessages = new ConjuntMessages();

        Document document;
        DocumentBuilder docBuilder;

        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        for(Lang lang : Lang.values()) {
            try {
                document = docBuilder.parse(BitsWorlds.class.getResourceAsStream("/lang/" + lang.name() + ".xml"));
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }

            NodeList classes = document.getElementsByTagName("class");

            for (int i = 0; i < classes.getLength(); i++) {
                List<String> sl = new ArrayList<>();
                StringBuilder sb = new StringBuilder();

                sl.add(classes.item(i).getAttributes().item(0).getTextContent());
                sl.add(".");

                NodeList messageNodes = ((Element) classes.item(i)).getElementsByTagName("message");

                Node parent = classes.item(i).getParentNode();

                while (parent.getNodeName().equals("package")) {
                    sl.add(parent.getAttributes().item(0).getTextContent());

                    parent = parent.getParentNode();

                    if (parent.getNodeName().equals("package"))
                        sl.add(".");
                }

                Collections.reverse(sl);
                sl.forEach(sb::append);

                if (!messages.containsKey(sb.toString()))
                    messages.put(sb.toString(), new ConjuntMessages());

                final ConjuntMessages mlMessagesList = messages.get(sb.toString());
                addNodeListToConjuntMessage(messageNodes, mlMessagesList, lang);

                NodeList utilMessageNodes = ((Element) document.getElementsByTagName("utils").item(0)).getElementsByTagName("message");
                addNodeListToConjuntMessage(utilMessageNodes, utilMessages, lang);

            }
        }

        return false;
    }


    private static void addNodeListToConjuntMessage(NodeList nodes, ConjuntMessages conjunt, Lang lang) {
        for (int f = 0; f < nodes.getLength(); f++) {
            String key = nodes.item(f).getAttributes().item(0).getTextContent();
            String message = nodes.item(f).getTextContent().replace("\\n", "\n");

            if (conjunt.containsKey(key)) {
                conjunt.appendLangMessage(key, new LangMessage(lang, message));
                continue;
            }

            conjunt.addMessage(key, new MLMessage(new LangMessage(lang, message)));
        }
    }

    /**
     * This method will get a message based on the class and the key given
     * @param c message class
     * @param key message key
     * @return the MLMessage based on the class and the key given
     * returns null if no message is found based on the class and key
     */
    @Nonnull
    public static MLMessage getClassMessage(Class c, String key) {
        if (messages.containsKey(c.getName())) {
            return messages.get(c.getName()).getMessage(key);
        }
        return null;
    }

    /**
     * This method will get a util message based on the key given
     * @param key message key
     * @return the MLMessage based on the key given
     * returns null if no message is found based on the class and key
     */
    @Nonnull
    public static MLMessage getUtilMessage(String key) {
        if (utilMessages.containsKey(key)) {
            return utilMessages.getMessage(key);
        }
        return null;
    }
}
