package bab.bitsworlds.cmd;

import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.multilanguage.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main Command
 */
public class BitsWorldsCmd implements CommandExecutor, TabCompleter {
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

        if (bwSender instanceof BWPlayer && !((BWPlayer) bwSender).getBukkitPlayer().hasPermission(bwcmd.getPermission())) {
            bwSender.sendMessage(PrefixMessage.permission_message);
            return true;
        }

        bwcmd.run(bwSender, cmd, alias, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        BWCommand configcmd = new ConfigCmd();

        if (args.length == 1) {
            if (sender.hasPermission(new HelpCmd().getPermission()))
                switch (LangCore.lang) {
                    case EN:
                        list.add("help");
                        break;
                    case PT:
                        list.add("ajuda");
                        break;
                    case SP:
                        list.add("ayuda");
                        break;
                    case FR:
                        list.add("aide");
                        break;
                }

            if (sender.hasPermission(configcmd.getPermission()))
                list.add("config");
        }

        else if (args.length >= 2) {
            if (sender.hasPermission(configcmd.getPermission()))
                list.addAll(configcmd.tabComplete(new BWCommandSender(sender), cmd, alias, args));
        }

        if (!args[args.length - 1].equals(""))
            list = list.stream().filter(string -> string.startsWith(args[args.length - 1])).collect(Collectors.toList());

        return list;
    }
}
