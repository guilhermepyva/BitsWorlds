package bab.bitsworlds.cmd;

import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPermission;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.BWGUI;
import bab.bitsworlds.gui.GUIItem;
import bab.bitsworlds.gui.ImplGUI;
import bab.bitsworlds.gui.MainGUI;
import bab.bitsworlds.multilanguage.LangCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;
import java.util.List;

public class ListWorldCmd implements BWCommand, ImplGUI {
    @Override
    public BWPermission getPermission() {
        return BWPermission.MAINCMD_WORLD_LIST;
    }

    @Override
    public void run(BWCommandSender sender, Command cmd, String alias, String[] args) {
        if (sender instanceof BWPlayer) {
            ((BWPlayer) sender).openGUI(getGUI("listworld_main", (BWPlayer) sender));
        }
    }

    @Override
    public List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args) {
        return null;
    }

    @Override
    public BWGUI getGUI(String code, BWPlayer player) {
        if ("listworld_main".equals(code)) {
            //TODO fazer em página
            return new ListWorldGUI(
                    "listworld_main",
                    5 * 9,
                    LangCore.getClassMessage(this.getClass(), "gui-title").toString(),
                    this,
                    true,
                    player
            );
        }
        throw new NullPointerException("No GUI with id " + code + " found");
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        switch (event.getSlot()) {
            case 36:
                if (gui.getItem(36) != null) {
                    player.openGUI(new MainGUI().getGUI("main", player));
                }
        }
    }

    private static class ListWorldGUI extends BWGUI {
        public BWPlayer player;

        public ListWorldGUI(String id, int size, String title, ImplGUI guiClass, boolean updatable, BWPlayer player) {
            super(id, size, title, guiClass, updatable);
        }

        @Override
        public void setupItem(int item) {
            switch (item) {
                case 36:
                    this.setItem(36, new GUIItem(
                            Material.SIGN,
                            ChatColor.GOLD + LangCore.getUtilMessage("back-item-title").toString(),
                            Collections.emptyList(),
                            LangCore.getUtilMessage("back-item-guide-mode"),
                            player
                    ));
                    break;
            }
        }

        @Override
        public BWGUI init() {
            return this;
        }

        @Override
        public void update() {

        }
    }
}
