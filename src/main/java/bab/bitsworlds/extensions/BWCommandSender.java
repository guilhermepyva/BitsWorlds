package bab.bitsworlds.extensions;

import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.LangMessage;
import bab.bitsworlds.multilanguage.MLMessage;
import bab.bitsworlds.multilanguage.PrefixMessage;
import bab.bitsworlds.task.BWTask;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * This is an extension class for Command Senders to work effectively with the plugin
 */
public class BWCommandSender {
    private CommandSender commandSender;

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public BWCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    /**
     * Send a {@link MLMessage} (Multi-Language Message)
     * <p>This method calls the {@link BWCommandSender#sendMessage(LangMessage)} with
     * the correct {@link LangMessage} that will be sent</p>
     * @param message the Multi-Language Message that will be sent
     */
    public void sendMessage(MLMessage message) {
        sendMessage(message.getTranslatedMessage());
    }

    /**
     * Send a {@link LangMessage}
     * @param langMessage the LangMessage that will be sent
     */
    public void sendMessage(LangMessage langMessage) {
        this.getCommandSender().sendMessage(langMessage.message);
    }

    /**
     * Send a message combined with several objects
     * <pre>This method just support three object: {@link MLMessage}, {@link LangMessage} and String
     *Of the {@link MLMessage} will be obtained the {@link MLMessage#getTranslatedMessage()}
     *Of the {@link LangMessage} will be obtaneid de {@link LangMessage#message}
     *And the String will be appended normally</pre>
     * @param objects the objects that will be read and appended to a final string
     */
    public void sendMessage(Object... objects) {
        StringBuilder finalString = new StringBuilder();
        for (Object object : objects) {
            if (object instanceof MLMessage)
                finalString.append(object.toString());

            else if (object instanceof LangMessage)
                finalString.append(((LangMessage) object).message);

            else if (object instanceof String)
                finalString.append((String) object);
        }

        this.getCommandSender().sendMessage(finalString.toString());
    }

    public void reportExceptionResponse(BWTask.BWExceptionResponse response) {
        sendMessage(PrefixMessage.error.getPrefix(),
                LangCore.getClassMessage(BWCommandSender.class, "exception-response")
                .setKey("%%exc", ChatColor.DARK_RED + response.exception.getMessage()));
    }

    /**
     * An shortcut to {@code CommandSender#sendMessage(String)}
     * @param message the String to be sent
     */
    public void sendMessage(String message) { this.getCommandSender().sendMessage(message); }

    public boolean hasPermission(BWPermission permission) {
        return commandSender.hasPermission(permission.string);
    }
}
