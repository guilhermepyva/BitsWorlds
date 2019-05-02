package bab.bitsworlds.cmd;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.db.SQLDataManager;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPermission;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.BWGUI;
import bab.bitsworlds.gui.ImplGUI;
import bab.bitsworlds.logger.Log;
import bab.bitsworlds.logger.LogCore;
import bab.bitsworlds.multilanguage.LangCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class LogCmd implements BWCommand, ImplGUI {
    @Override
    public BWPermission getPermission() {
        return BWPermission.LOGS_SEE;
    }

    @Override
    public void run(BWCommandSender sender, Command cmd, String alias, String[] args) {
        ((BWPlayer) sender).openGUI(getGUI("main", (BWPlayer) sender));
    }

    @Override
    public BWGUI getGUI(String code, BWPlayer player) {
        switch (code) {
            case "main":
                return new BWGUI(
                        "main",
                        6*9,
                        LangCore.getClassMessage(LogCmd.class, "gui-title").toString(),
                        this
                ) {
                    @Override
                    public void setupItem(int item) {
                        switch (item) {
                            case 0:
                                Bukkit.getScheduler().runTaskAsynchronously(BitsWorlds.plugin, () -> {
                                    try {
                                        int i = 0;
                                        for (Log log : SQLDataManager.queryGlobalLogs()) {
                                            this.setItem(i, LogCore.getItemFromLog(log));

                                            i++;
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                });

                                break;
                        }
                    }

                    @Override
                    public BWGUI init() {
                        genItems(0);

                        return this;
                    }
                }.init();
        }

        return null;
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {

    }

    @Override
    public List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args) {
        return Collections.emptyList();
    }
}
