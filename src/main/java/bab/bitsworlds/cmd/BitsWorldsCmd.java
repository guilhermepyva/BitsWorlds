package bab.bitsworlds.cmd;

import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.MainGUI;
import bab.bitsworlds.multilanguage.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
            if (!(bwSender instanceof BWPlayer)) {
                bwSender.sendMessage(
                        PrefixMessage.error.getPrefix(),
                        LangCore.getClassMessage(this.getClass(), "cmdsender-cant-run-cmd")
                );
                return true;
            }
            ((BWPlayer) bwSender).openGUI(new MainGUI().getGUI("main", (BWPlayer) bwSender));
            return true;
        }

        switch (args[0].toUpperCase()) {
            case "CONFIG":
                bwcmd = new ConfigCmd();
                break;
            case "LOG":
                bwcmd = new LogCmd();
                break;
            default:
                bwSender.sendMessage(
                        PrefixMessage.warn.getPrefix(),
                        LangCore.getClassMessage(getClass(), "not-valid-argument")
                                .setKey("%%arg", ChatColor.ITALIC + args[0] + PrefixMessage.warn.getDefaultChatColor())
                                .setKey("%%cmd", ChatColor.BOLD + "/BitsWorlds" + ChatColor.ITALIC)
                                .setKey("%%prefixColor", PrefixMessage.warn.getDefaultChatColor().toString())
                );
                return true;
        }

        if (bwSender instanceof BWPlayer && bwSender.hasPermission(bwcmd.getPermission())) {
            bwSender.sendMessage(PrefixMessage.permission_message);
            return true;
        }

        bwcmd.run(bwSender, cmd, alias, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        BWCommand configCmd = new ConfigCmd();
        BWCommandSender bwSender = new BWCommandSender(sender);

        if (args.length == 1) {
            if (bwSender.hasPermission(new HelpCmd().getPermission()))
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

            if (bwSender.hasPermission(configCmd.getPermission()))
                list.add("config");
        }

        else if (args.length >= 2) {
            if (bwSender.hasPermission(configCmd.getPermission()))
                list.addAll(configCmd.tabComplete(bwSender, cmd, alias, args));
        }

        if (!args[args.length - 1].equals(""))
            list = list.stream().filter(string -> string.startsWith(args[args.length - 1])).collect(Collectors.toList());

        return list;
    }
}
