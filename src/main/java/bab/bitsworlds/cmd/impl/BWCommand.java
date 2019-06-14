package bab.bitsworlds.cmd.impl;

import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPermission;
import org.bukkit.command.Command;

import java.util.List;

/**
 * Simple interface to facilitate the work of commands with the plugin
 */
public interface BWCommand {
    BWPermission getPermission();

    void run(BWCommandSender sender, Command cmd, String alias, String[] args);

    List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args);
}
