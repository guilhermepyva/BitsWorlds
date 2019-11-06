package bab.bitsworlds.cmd;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.ChatInput;
import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPermission;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.BWGUI;
import bab.bitsworlds.gui.BWPagedGUI;
import bab.bitsworlds.gui.GUIItem;
import bab.bitsworlds.gui.ImplGUI;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import bab.bitsworlds.utils.BackupUtils;
import bab.bitsworlds.utils.WorldUtils;
import bab.bitsworlds.world.BWBackup;
import bab.bitsworlds.world.BWUnloadedWorld;
import bab.bitsworlds.world.BWorld;
import bab.bitsworlds.world.WorldCreator;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListBackupCmd implements BWCommand, ImplGUI {
    @Override
    public BWPermission getPermission() {
        return BWPermission.MAINCMD_BACKUP_LIST;
    }

    @Override
    public void run(BWCommandSender sender, Command cmd, String alias, String[] args) {

    }

    @Override
    public List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args) {
        return null;
    }

    @Override
    public BWGUI getGUI(String code, BWPlayer player) {
        return new ListBackupGui(
                "main_backup_list",
                5 * 9,
                LangCore.getClassMessage(getClass(), "gui-title").toString(),
                this,
                true,
                player
        );
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        ListBackupGui listBackupGui = (ListBackupGui) gui;

        if (event.getSlot() <= 35) {
            if (listBackupGui.itemsID.size() - 1 >= event.getSlot() && listBackupGui.itemsID.get(event.getSlot()) != null) {
                BWBackup backup = listBackupGui.itemsID.get(event.getSlot());

                if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    if (player.hasPermission(BWPermission.DELETE_BACKUP))
                        Bukkit.getScheduler().runTaskAsynchronously(
                                BitsWorlds.plugin,
                                () -> {
                                    player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(getClass(), "delete-backup-confirmation-message"));
                                    player.getBukkitPlayer().closeInventory();

                                    String input = ChatInput.askPlayer(player);

                                    if (input.equalsIgnoreCase("y")) {
                                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(getClass(), "deleting-backup"));
                                        try {
                                            FileUtils.deleteDirectory(backup.file);
                                        } catch (IOException e) {
                                            player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(getClass(), "error-deleting-message"));
                                            e.printStackTrace();
                                        }
                                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(getClass(), "deleted-backup"));
                                    }

                                    listBackupGui.setupItem(0);

                                    player.openGUI(listBackupGui);
                                }
                        );
                }
                else {
                    if (player.hasPermission(BWPermission.RECOVER_BACKUP)) {
                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(ListBackupCmd.class, "recovering-message"));
                        if (new File(Bukkit.getWorldContainer() + "/" + backup.file.getName()).exists()) {
                            player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(ListBackupCmd.class, "backup-name-already-exists"));
                            return;
                        }
                        try {
                            BackupUtils.recoverBackup(backup.file);
                        } catch (IOException e) {
                            player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(ListBackupCmd.class, "error-recovering-message"));
                            e.printStackTrace();
                        }

                        if (!(new File(Bukkit.getWorldContainer() + "/" + backup.file.getName()).exists())) {
                            player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(ListBackupCmd.class, "error-recovering-message"));
                            return;
                        }

                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(ListBackupCmd.class, "recovered-message"));
                        if (player.hasPermission(BWPermission.MAINCMD_WORLD_INTERACT)) {
                            InteractWorldCmd cmd = new InteractWorldCmd();
                            InteractWorldCmd.InteractWorldGUI interactWorldGUI = (InteractWorldCmd.InteractWorldGUI) cmd.getGUI("", player);
                            interactWorldGUI.world = new BWUnloadedWorld(new File(Bukkit.getWorldContainer() + "/" + backup.file.getName()));
                            player.openGUI(interactWorldGUI.init());
                        }
                    }
                }
            }
            return;
        }

        switch (event.getSlot()) {
            case 36:
                if (player.hasPermission(BWPermission.MAINCMD_WORLD_INTERACT)) {
                    InteractWorldCmd.InteractWorldGUI interactWorldGUI = (InteractWorldCmd.InteractWorldGUI) new InteractWorldCmd().getGUI("", player);
                    interactWorldGUI.world = listBackupGui.returnItemWorld;
                    player.openGUI(interactWorldGUI.init());
                    if (listBackupGui.returnItemFromInteractWorld)
                        interactWorldGUI.genItems(36);
                    break;
                }
            case 39:
                if (listBackupGui.actualPage > 0) {
                    listBackupGui.actualPage--;
                    listBackupGui.setupItemPage(41, 39);
                    listBackupGui.setupItem(0);
                }
                break;
            case 41:
                if (listBackupGui.actualPage < listBackupGui.lastPage) {
                    listBackupGui.actualPage++;
                    listBackupGui.setupItemPage(41, 39);
                    listBackupGui.setupItem(0);
                }
                break;
        }
    }

    public class ListBackupGui extends BWPagedGUI<List<BWBackup>> {
        public String filter;
        public BWorld returnItemWorld;
        public boolean returnItemFromInteractWorld;
        BWPlayer player;

        public ListBackupGui(String id, int size, String title, ImplGUI guiClass, boolean updatable, BWPlayer player) {
            super(id, size, title, guiClass, updatable);
            this.player = player;
        }

        @Override
        public void setupItem(int item) {
            switch (item) {
                case 0:
                    this.itemsID = new ArrayList<>();
                    int i = 0;
                    for (int i1 = 0; i1 < 36; i1++) {
                        setItem(i1, new ItemStack(Material.AIR));
                    }
                    for (BWBackup backup : queryBackups(actualPage * 36)) {
                        List<String> description = new ArrayList<>();

                        description.add(ChatColor.GOLD + LangCore.getClassMessage(ListBackupCmd.class, "world").setKey("%%w", ChatColor.WHITE + backup.worldName).toString());
                        description.add(ChatColor.GOLD + LangCore.getClassMessage(ListBackupCmd.class, "made-in").setKey("%%t", ChatColor.WHITE + LangCore.getDateByPattern(backup.timestamp.toLocalDateTime())).toString());
                        description.add("");
                        if (player.hasPermission(BWPermission.RECOVER_BACKUP))
                            description.add(ChatColor.WHITE + LangCore.getClassMessage(ListBackupCmd.class, "click").toString());
                        if (player.hasPermission(BWPermission.DELETE_BACKUP))
                            description.add(ChatColor.DARK_RED + LangCore.getClassMessage(ListBackupCmd.class, "shift-click").toString());
                        this.setItem(i, new GUIItem(
                                Material.EYE_OF_ENDER,
                                backup.toString(),
                                description
                        ));
                        this.itemsID.add(i, backup);
                        i++;
                    }
                    break;
                case 36:
                    this.setItem(36, new GUIItem(
                            Material.SIGN,
                            ChatColor.GOLD + LangCore.getUtilMessage("back-item-title").toString(),
                            Collections.emptyList(),
                            LangCore.getUtilMessage("back-item-guide-mode"),
                            player
                    ));
                    break;
                case 39:
                    this.setItem(39, new GUIItem(
                            Material.ARROW,
                            ChatColor.GOLD.toString() + LangCore.getUtilMessage("page").toString() + " " + (this.actualPage),
                            Collections.emptyList()
                    ));
                    break;
                case 41:
                    this.setItem(41, new GUIItem(
                            Material.ARROW,
                            ChatColor.GOLD.toString() + LangCore.getUtilMessage("page").toString() + " " + (this.actualPage + 2),
                            Collections.emptyList()
                    ));
                    break;
            }
        }

        @Override
        public BWGUI init() {
            this.lastPage = calculateLastPage();
            this.actualPage = 0;
            genItems(0, 36);

            this.setupItemPage(41, 39);

            return this;
        }

        @Override
        public void update() {
            this.lastPage = calculateLastPage();
            init();

            this.setupItemPage(41, 39);
        }

        public List<BWBackup> queryBackups(int offset) {
            List<BWBackup> backups = new ArrayList<>();
            int i = 0;
            for (BWBackup world : queryByFilter()) {
                if (i >= offset) {
                    if ((i - offset) > 35) {
                        break;
                    }
                    backups.add(world);
                }

                i++;
            }

            return backups;
        }

        int calculateLastPage() {
            return (countByFilter() - 1) / 36;
        }

        List<BWBackup> queryByFilter() {
            if (filter == null)
                return BackupUtils.getBackups();
            else
                return BackupUtils.getBackupsByWorld(filter);
        }

        public int countByFilter() {
            if (filter == null)
                return BackupUtils.getBackups().size();
            else
                return BackupUtils.getBackupsByWorld(filter).size();
        }
    }
}
