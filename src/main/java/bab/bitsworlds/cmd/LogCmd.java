package bab.bitsworlds.cmd;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.db.SQLDataManager;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPermission;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.*;
import bab.bitsworlds.logger.Log;
import bab.bitsworlds.logger.LogCore;
import bab.bitsworlds.multilanguage.LangCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
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
        if (code.equals("main")) {
            return new BWPagedGUI(
                    "paged_log",
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
                                this.itemsID = new ArrayList<Integer>();

                                for (int i = 0; i < 45; i++) {
                                    setItem(i, new ItemStack(Material.AIR));
                                }

                                int i = 0;
                                int skipItems = this.actualPage * 45;
                                for (Log log : queryLogs(skipItems)) {
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
                                    ((ArrayList<Integer>) this.itemsID).add(log.id);

                                    i++;
                                    if (i == 45) {
                                        break;
                                    }
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
                                    ChatColor.GOLD.toString() + LangCore.getUtilMessage("page").toString() + " " + (this.actualPage),
                                    Collections.emptyList()
                            ));
                            break;
                        case 50:
                            this.setItem(50, new GUIItem(
                                    Material.ARROW,
                                    ChatColor.GOLD.toString() + LangCore.getUtilMessage("page").toString() + " " + (this.actualPage + 2),
                                    Collections.emptyList()
                            ));
                            break;
                    }
                }



                @Override
                public void update() {
                    genItems(0, 45);
                    this.lastPage = calculateLastPage();

                    setupItemPage(this);
                }

                @Override
                public BWGUI init() {
                    genItems(0, 45);

                    this.actualPage = 0;
                    this.lastPage = calculateLastPage();

                    setupItemPage(this);

                    return this;
                }

                int calculateLastPage() {
                    try {
                        /*int i = 0;
                        for (Log ignored : SQLDataManager.queryGlobalLogs()) {
                            i++;

                            if (i == 45) {
                                i = 1;
                                lastPage++;
                            }
                        }*/

                        return (int) Math.floor((double) SQLDataManager.queryCountLogs() / 45);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }

                List<Log> queryLogs(int offset) {
                    try {
                        return SQLDataManager.queryLogs(" LIMIT " + offset + ", " + 45);
                    } catch (SQLException e) {
                        e.printStackTrace();

                        return null;
                    }
                }
            };
        }

        return null;
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        switch (event.getSlot()) {
            case 45:
                if (gui.getItem(27) != null) {
                    player.openGUI(new MainGUI().getGUI("main", player));
                }
                break;
                //TODO LEMBRAR DE FAZER O ITEM APARECER SOMENTE QUANDO A PESSOA USAR A JANELA PRINCIPAL DO PL
            case 48:
                BWPagedGUI pagedGUI = (BWPagedGUI) gui;
                if (pagedGUI.actualPage > 0) {
                    pagedGUI.actualPage--;
                    setupItemPage(pagedGUI);
                    pagedGUI.setupItem(0);
                }
                break;
            case 50:
                pagedGUI = (BWPagedGUI) gui;
                if (pagedGUI.actualPage < pagedGUI.lastPage) {
                    pagedGUI.actualPage++;
                    setupItemPage(pagedGUI);
                    pagedGUI.setupItem(0);
                }
                break;
        }
    }

    void setupItemPage(BWPagedGUI pagedGUI) {
        pagedGUI.setItem(48, new ItemStack(Material.AIR));
        pagedGUI.setItem(50, new ItemStack(Material.AIR));

        if (pagedGUI.actualPage > 0) {
            pagedGUI.setupItem(48);
        }
        if (pagedGUI.actualPage < pagedGUI.lastPage) {
            pagedGUI.setupItem(50);
        }
    }

    @Override
    public List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args) {
        return Collections.emptyList();
    }
}
