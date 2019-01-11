package bab.bitsworlds;

import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangMessage;
import bab.bitsworlds.multilanguage.MLMessage;
import bab.bitsworlds.multilanguage.PrefixMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }
}
