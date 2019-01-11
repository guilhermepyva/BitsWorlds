package bab.bitsworlds.cmd;

import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.multilanguage.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Main Command
 */
public class BitsWorldsCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        BWCommand bwcmd;

        BWCommandSender bwSender;
        if (sender instanceof Player)
            bwSender = new BWPlayer((Player) sender);
        else {
            bwSender = new BWCommandSender(sender);
        }

        if (args.length <= 0) {
            bwSender.sendMessage(
                    PrefixMessage.warn.getPrefix(),
                    LangCore.getClassMessage(getClass(), "must_insert_argument")
                            .setKey("%%cmdhelp", ChatColor.BOLD + "/BitsWorlds " + ChatColor.ITALIC + "help" + PrefixMessage.warn.getDefaultChatColor())
                            .setKey("%%cmd", ChatColor.BOLD + "/BitsWorlds" + ChatColor.ITALIC)
                            .setKey("%%prefixColor", PrefixMessage.warn.getDefaultChatColor().toString())
            );
            return true;
        }

        switch (args[0].toUpperCase()) {
            case "CONFIG":
                bwcmd = new ConfigCmd();
                break;

            default:
                bwSender.sendMessage(
                        PrefixMessage.warn.getPrefix(),
                        LangCore.getClassMessage(getClass(), "not_valid_argument")
                                .setKey("%%arg", ChatColor.ITALIC + args[0] + PrefixMessage.warn.getDefaultChatColor())
                                .setKey("%%cmd", ChatColor.BOLD + "/BitsWorlds " + ChatColor.ITALIC + "help" + PrefixMessage.warn.getDefaultChatColor())
                                .setKey("%%prefix", PrefixMessage.warn.getPrefix())
                );
                return true;
        }

        bwcmd.run(bwSender, cmd, alias, args);

        return true;
    }
}
