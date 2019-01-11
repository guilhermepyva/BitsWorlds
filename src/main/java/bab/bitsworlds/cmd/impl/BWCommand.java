package bab.bitsworlds.cmd.impl;

import bab.bitsworlds.extensions.BWCommandSender;
import org.bukkit.command.Command;

/**
 * Simple interface to facilitate the work of commands with the plugin
 */
public interface BWCommand {
    String permission = null;

    void run(BWCommandSender sender, Command cmd, String alias, String[] args);
}
