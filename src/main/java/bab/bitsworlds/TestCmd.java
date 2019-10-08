package bab.bitsworlds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        for (String gameRule : player.getWorld().getGameRules()) {
            System.out.println(gameRule);
            System.out.println(player.getWorld().getGameRuleValue(gameRule));
            System.out.println("---");
        }

        return true;
    }
}
