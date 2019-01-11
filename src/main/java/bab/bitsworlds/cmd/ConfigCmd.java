package bab.bitsworlds.cmd;

import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.BWGUI;
import bab.bitsworlds.gui.ImplGUI;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ConfigCmd implements BWCommand, ImplGUI {
    public void run(BWCommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            if (!(commandSender instanceof BWPlayer)) {
                commandSender.sendMessage(
                        PrefixMessage.error.getPrefix(),
                        LangCore.getClassMessage(this.getClass(), "cmdsender-cant-run-cmd")
                );
                return;
            }

            BWPlayer player = (BWPlayer) commandSender;

            player.openGUI(getGUIs().get(0));
        }
    }

    public Map<Integer, BWGUI> getGUIs() {
        Map<Integer, BWGUI> guis = new HashMap<>();

        BWGUI mainGui = new BWGUI(
                4*9,
                ChatColor.AQUA + LangCore.getClassMessage(this.getClass(), "gui-title").setKey("%%name", "BitsWorlds").getTranslatedMessage().message,
                this
        );

        mainGui.setItem(0, new ItemStack(Material.BANNER));

        guis.put(0, mainGui);

        return guis;
    }

    @Override
    public void clickEvent(InventoryClickEvent event) {
        System.out.println("a");
    }
}