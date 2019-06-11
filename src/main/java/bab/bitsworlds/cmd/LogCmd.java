package bab.bitsworlds.cmd;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.db.SQLDataManager;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPermission;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.BWGUI;
import bab.bitsworlds.gui.GUIItem;
import bab.bitsworlds.gui.ImplGUI;
import bab.bitsworlds.logger.Log;
import bab.bitsworlds.logger.LogCore;
import bab.bitsworlds.multilanguage.LangCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Collection;
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
                        this,
                        true
                ) {
                    @Override
                    public void setupItem(int item) {
                        switch (item) {
                            case 0:
                                Bukkit.getScheduler().runTaskAsynchronously(BitsWorlds.plugin, () -> {
                                    try {
                                        int i = 0;
                                        for (Log log : SQLDataManager.queryGlobalLogs()) {
                                            GUIItem logitem = LogCore.getItemFromLog(log);

                                            if (player.hasPermission(BWPermission.LOGS_NOTE_ADD) || player.hasPermission(BWPermission.LOGS_NOTE_MODIFY)) {
                                                ItemMeta logitemeta = logitem.getItemMeta();

                                                List<String> logitemlore = logitemeta.getLore();
                                                if (log.note == null && player.hasPermission(BWPermission.LOGS_NOTE_ADD)) {
                                                    logitemlore.add("");

                                                    logitemlore.addAll(
                                                            GUIItem.loreJumper(LangCore.getClassMessage(LogCmd.class, "add-note").toString(), 30, ChatColor.AQUA.toString(), "")
                                                    );
                                                }

                                                else if (log.note != null && player.hasPermission(BWPermission.LOGS_NOTE_MODIFY)) {
                                                    logitemlore.add("");

                                                    logitemlore.addAll(
                                                            GUIItem.loreJumper(LangCore.getClassMessage(LogCmd.class, "modify-note").toString(), 30, ChatColor.AQUA.toString(), "")
                                                    );
                                                }

                                                logitemeta.setLore(logitemlore);
                                                logitem.setItemMeta(logitemeta);
                                            }

                                            this.setItem(i, logitem);

                                            i++;
                                            if (i == 45) {
                                                break;
                                            }
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                });

                                break;
                            case 45:
                                this.setItem(45, new GUIItem(
                                        Material.SIGN,
                                        ChatColor.GOLD + LangCore.getUtilMessage("back-item-title").toString(),
                                        Collections.emptyList(),
                                        LangCore.getUtilMessage("back-item-guide-mode"),
                                        player
                                ));

                                break;
                            case 48:
                                this.setItem(48, new GUIItem(
                                        Material.ARROW,
                                        ChatColor.GOLD + LangCore.getUtilMessage("back-page").toString(),
                                        Collections.emptyList()
                                ));
                                break;
                            case 50:
                                this.setItem(50, new GUIItem(
                                        Material.ARROW,
                                        ChatColor.GOLD + LangCore.getUtilMessage("next-page").toString(),
                                        Collections.emptyList()
                                ));
                                break;
                        }
                    }

                    @Override
                    public BWGUI init() {
                        genItems(0, 45, 48, 50);

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
