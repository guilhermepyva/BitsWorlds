package bab.bitsworlds.cmd;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.ChatInput;
import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.db.SQLDataManager;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPermission;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.*;
import bab.bitsworlds.logger.Log;
import bab.bitsworlds.logger.LogCore;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import bab.bitsworlds.world.BWorld;
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
import java.util.UUID;

public class LogCmd implements BWCommand, ImplGUI {
    @Override
    public BWPermission getPermission() {
        return BWPermission.LOGS_SEE;
    }

    @Override
    public void run(BWCommandSender sender, Command cmd, String alias, String[] args) {
        ((BWPlayer) sender).openGUI(getGUI("global", (BWPlayer) sender));
    }

    @Override
    public List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public BWGUI getGUI(String code, BWPlayer player) {
        LogGUI logGUI =  new LogGUI(
                "main_logs",
                6*9,
                LangCore.getClassMessage(LogCmd.class, "gui-title").toString(),
                this,
                true,
                player
        );

        if ("plugin".equals(code))
            logGUI.filter = Filter.PLUGIN;

        if ("world".equals(code))
            logGUI.filter = Filter.WORLDS;

        return logGUI.init();
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        LogGUI logGUI = (LogGUI) gui;

        if (event.getSlot() < 45) {
            if (!LogCore.notes)
                return;

            if (logGUI.itemsID.size() - 1 < event.getSlot())
                return;

            Bukkit.getScheduler().runTaskAsynchronously(BitsWorlds.plugin, () -> {
                int logID = logGUI.itemsID.get(event.getSlot());

                player.closeInventory();

                player.sendMessage(PrefixMessage.info.getPrefix() + LangCore.getClassMessage(LogCmd.class, "type-note"));

                String note = ChatInput.askPlayer(player);
                try {
                    SQLDataManager.updateNoteLog(logID, note, player.getBukkitPlayer().getUniqueId().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                player.sendMessage(PrefixMessage.info.getPrefix() + LangCore.getClassMessage(LogCmd.class, "note-appended-success"));

                player.openGUI(gui);

                GUICore.updateGUI(gui.id);
            });

            return;
        }

        switch (event.getSlot()) {
            case 45:
                if (gui.getItem(45) != null) {
                    if (logGUI.returnItem != null) {
                        InteractWorldCmd.InteractWorldGUI interactGui = (InteractWorldCmd.InteractWorldGUI) new InteractWorldCmd().getGUI("main", player);
                        interactGui.world = logGUI.returnItem;
                        player.openGUI(interactGui.init());
                        interactGui.genItems(36);
                        return;
                    }
                    player.openGUI(new MainGUI().getGUI("main", player));
                }
                break;
            case 48:
                if (logGUI.actualPage > 0) {
                    logGUI.actualPage--;
                    logGUI.setupItemPage(50, 48);
                    logGUI.setupItem(0);
                }
                break;
            case 50:
                if (logGUI.actualPage < logGUI.lastPage) {
                    logGUI.actualPage++;
                    logGUI.setupItemPage(50, 48);
                    logGUI.setupItem(0);
                }
                break;
        }
    }

    public class LogGUI extends BWPagedGUI<List<Integer>> {
        private BWPlayer player;
        public Filter filter;
        public String worldNameFilter;
        public UUID worldUIDFilter;
        public BWorld returnItem;

        public LogGUI(String id, int size, String title, ImplGUI guiClass, boolean updatable, BWPlayer player) {
            super(id, size, title, guiClass, updatable);
            this.player = player;
        }

        @Override
        public void setupItem(int item) {
            switch (item) {
                case 0:
                    Bukkit.getScheduler().runTaskAsynchronously(BitsWorlds.plugin, () -> {
                        this.itemsID = new ArrayList<>();

                        for (int i = 0; i < 45; i++)
                            setItem(i, new ItemStack(Material.AIR));

                        int i = 0;
                        int skipItems = this.actualPage * 45;
                        for (Log log : queryLogs(skipItems)) {
                            GUIItem logitem = LogCore.getItemFromLog(log);

                            if ((player.hasPermission(BWPermission.LOGS_NOTE_ADD) || player.hasPermission(BWPermission.LOGS_NOTE_MODIFY)) && LogCore.notes) {
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
                            this.itemsID.add(log.id);

                            i++;
                            if (i == 45) {
                                break;
                            }
                        }
                    });

                    break;
                case 45:
                    this.setItem(45, new GUIItem(
                            Material.LEGACY_SIGN,
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
            this.lastPage = calculateLastPage();
            init();

            this.setupItemPage(50, 48);
        }

        @Override
        public BWGUI init() {
            setupItem(0);

            this.actualPage = 0;
            this.lastPage = calculateLastPage();

            this.setupItemPage(50, 48);

            return this;
        }

        int calculateLastPage() {
            try {
                return (int) Math.floor((double) SQLDataManager.queryCountLogs() / 45);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }

        List<Log> queryLogs(int offset) {
            try {
                if (filter == null)
                    return SQLDataManager.queryLogs(" LIMIT " + offset + ", " + 45, "");
                else if (filter == Filter.PLUGIN)
                    return SQLDataManager.queryLogs(" LIMIT " + offset + ", " + 45, " WHERE worldName IS NULL");
                else if (filter == Filter.WORLDNAME)
                    return SQLDataManager.queryLogs(" LIMIT " + offset + ", " + 45, " WHERE worldName = '" + worldNameFilter + "'");
                else if (filter == Filter.WORLDUUID)
                    return SQLDataManager.queryLogs(" LIMIT " + offset + ", " + 45, " WHERE world = '" + worldUIDFilter.toString() + "'");
                else
                    return SQLDataManager.queryLogs(" LIMIT " + offset + ", " + 45, " WHERE worldName IS NOT NULL");
            } catch (SQLException e) {
                e.printStackTrace();

                return null;
            }
        }
    }

    public enum Filter {
        WORLDNAME,
        WORLDUUID,
        WORLDS,
        PLUGIN
    }
}