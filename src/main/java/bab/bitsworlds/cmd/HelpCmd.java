package bab.bitsworlds.cmd;

import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.BWCommandSender;
import org.bukkit.command.Command;

import java.util.List;

public class HelpCmd implements BWCommand {
    @Override
    public String getPermission() {
        return "bitsworlds.maincmd";
    }

    @Override
    public void run(BWCommandSender sender, Command cmd, String alias, String[] args) {

    }

    @Override
    public List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args) {
        return null;
    }
}
