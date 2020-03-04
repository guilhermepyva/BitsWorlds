package bab.bitsworlds.gui;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.ChatInput;
import bab.bitsworlds.SkullCore;
import bab.bitsworlds.cmd.*;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import bab.bitsworlds.utils.WorldUtils;
import bab.bitsworlds.world.BWLoadedWorld;
import bab.bitsworlds.world.BWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;

public class MainGUI implements ImplGUI {
    @Override
    public BWGUI getGUI(String code, BWPlayer player) {
        switch (code) {
            case "main":
                return new BWGUI(
                        "main",
                        4*9,
                        "BitsWorlds",
                        this,
                        true) {
                    @Override
                    public void setupItem(int item) {
                        switch (item) {
                            case 4:
                                GUIItem icon = new GUIItem(
                                        Material.SKULL_ITEM,
                                        1,
                                        (short) 3,
                                        ChatColor.GREEN + "" + ChatColor.BOLD + "BitsWorlds",
                                        Arrays.asList(ChatColor.WHITE + LangCore.getClassMessage(MainGUI.class, "version-word").setKey("%%v", BitsWorlds.plugin.getDescription().getVersion()).toString(),
                                                "",
                                                ChatColor.WHITE + "" + ChatColor.ITALIC + LangCore.getClassMessage(MainGUI.class, "by-word").setKey("%%n", ChatColor.BLUE + "MrPiva").toString()),
                                        LangCore.getClassMessage(MainGUI.class, "head-item-guide-mode"),
                                        player
                                );
                                SkullMeta meta = (SkullMeta) icon.getItemMeta();
                                SkullCore.applyToSkull(meta, SkullCore.Skull.BWICON);
                                icon.setItemMeta(meta);
                                this.setItem(4, icon);
                                break;
                            case 7:
                                this.setItem(7, new GUIItem(
                                        Material.STAINED_GLASS_PANE,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "interact-with-actual-world-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "interact-with-actual-world-tem-guide-mode"),
                                        player
                                ));

                                break;
                            case 8:
                                List<String> guideItemLore = new ArrayList<>();

                                guideItemLore.add(ChatColor.WHITE + LangCore.getClassMessage(MainGUI.class, "status-word").setKey("%%s",
                                        GUICore.guideMode(player) ? LangCore.getUtilMessage("enabled-word") : LangCore.getUtilMessage("disabled-word")).toString());

                                guideItemLore.add("");

                                guideItemLore.addAll(GUIItem.loreJumper(LangCore.getClassMessage(MainGUI.class, "guide-mode-lore").toString(), ChatColor.WHITE.toString(), ""));

                                GUIItem guideModeItem = new GUIItem(
                                        Material.PAPER,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "guide-mode-title").toString(),
                                        guideItemLore);

                                if (GUICore.guideMode(player))
                                    guideModeItem.addEffect();

                                ItemMeta guideModeItemItemMeta = guideModeItem.getItemMeta();

                                guideModeItemItemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

                                guideModeItem.setItemMeta(guideModeItemItemMeta);

                                this.setItem(8, guideModeItem);
                                break;
                            case 0:
                                this.setItem(0, new GUIItem(
                                        Material.REDSTONE,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "config-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "config-item-guide-mode").setKey("%%file", ChatColor.ITALIC + "config.yml"),
                                        player
                                ));

                                break;
                            case 19:
                                this.setItem(19, new GUIItem(
                                        Material.NAME_TAG,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "interact-word-by-name-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "interact-word-by-name-item-guide-mode"),
                                        player
                                ));
                            case 21:
                                GUIItem listworlditem = new GUIItem(
                                        Material.SKULL_ITEM,
                                        1,
                                        (short) 3,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "list-world-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "list-world-item-guide-mode").setKey("%%file", ChatColor.ITALIC + "config.yml"),
                                        player
                                );

                                meta = (SkullMeta) listworlditem.getItemMeta();
                                SkullCore.applyToSkull(meta, SkullCore.Skull.LISTWORLDICON);
                                listworlditem.setItemMeta(meta);
                                this.setItem(21, listworlditem);
                                break;
                            case 23:
                                GUIItem createWorldItem = new GUIItem(
                                        Material.SKULL_ITEM,
                                        1,
                                        (short) 3,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "create-world-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "create-world-item-guide-mode"),
                                        player
                                );
                                SkullMeta createWorldItemItemMeta = (SkullMeta) createWorldItem.getItemMeta();
                                SkullCore.applyToSkull(createWorldItemItemMeta, SkullCore.Skull.CREATEWORLDICON);
                                createWorldItem.setItemMeta(createWorldItemItemMeta);
                                this.setItem(23, createWorldItem);
                            case 25:
                                this.setItem(25, new GUIItem(
                                        Material.ENCHANTED_BOOK,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "list-backups-by-name-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "list-backups-by-name-item-guide-mode"),
                                        player
                                ));
                                break;
                            case 30:
                                this.setItem(30, new GUIItem(
                                        Material.BOOK,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "global-log-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "global-log-item-guide-mode"),
                                        player
                                ));
                                break;
                            case 31:
                                this.setItem(31, new GUIItem(
                                        Material.BOOK,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "plugin-log-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "plugin-log-item-guide-mode"),
                                        player
                                ));
                                break;
                            case 32:
                                this.setItem(32, new GUIItem(
                                        Material.BOOK,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "world-log-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "world-log-item-guide-mode"),
                                        player
                                ));
                                break;
                        }
                    }

                    @Override
                    public void update() {
                        init();
                    }

                    @Override
                    public BWGUI init() {
                        genItems(4, 8, 0, 21, 23, 30, 31, 32, 19, 7, 25);

                        return this;
                    }
                }.init();
        }

        throw new NullPointerException("No GUI with id " + code + " found");
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        switch (event.getSlot()) {
            case 7:
                InteractWorldCmd interactWorldCmd = new InteractWorldCmd();
                InteractWorldCmd.InteractWorldGUI interactGui = (InteractWorldCmd.InteractWorldGUI) interactWorldCmd.getGUI("main", player);
                interactGui.world = new BWLoadedWorld(player.getBukkitPlayer().getWorld());
                player.openGUI(interactGui.init());
                interactGui.genItems(36);
                break;
            case 8:
                GUICore.alternateGuideMode(player);

                gui.init();
                break;
            case 0:
                ConfigCmd configCmd = new ConfigCmd();

                if (!player.hasPermission(configCmd.getPermission())) {
                    player.sendMessage(PrefixMessage.permission_message);

                    player.getBukkitPlayer().closeInventory();

                    return;
                }
                BWGUI configCmdGUI = configCmd.getGUI("config_main",  player);

                player.openGUI(configCmdGUI);

                configCmdGUI.genItems(27);

                break;
            case 19:
                Bukkit.getScheduler().runTaskAsynchronously(
                        BitsWorlds.plugin,
                        () -> {
                            player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(MainGUI.class, "insert-world-name-to-interact-to"));
                            player.getBukkitPlayer().closeInventory();

                            String input = ChatInput.askPlayer(player);
                            if (input.equals("!")) {
                                player.openGUI(gui);
                                return;
                            }

                            String worldName = WorldUtils.getValidWorldName(input);

                            if (worldName.isEmpty()) {
                                player.openGUI(gui);
                                player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(CreateWorldCmd.class, "invalid-world-name"));
                                return;
                            }

                            List<BWorld> unloadedWorlds = WorldUtils.getUnloadedWorlds();
                            List<BWorld> loadedWorlds = WorldUtils.getLoadedWorlds();

                            Optional<BWorld> world = unloadedWorlds.stream().filter(bWorld -> bWorld.getName().equals(worldName)).findFirst();
                            if (!world.isPresent()) {
                                world = unloadedWorlds.stream().filter(bWorld -> bWorld.getName().equalsIgnoreCase(worldName)).findFirst();
                            }
                            if (!world.isPresent()) {
                                world = loadedWorlds.stream().filter(bWorld -> bWorld.getName().equals(worldName)).findFirst();
                            }
                            if (!world.isPresent()) {
                                world = loadedWorlds.stream().filter(bWorld -> bWorld.getName().equalsIgnoreCase(worldName)).findFirst();
                            }

                            if (world.isPresent()) {
                                InteractWorldCmd interactWorldCmd1 = new InteractWorldCmd();
                                InteractWorldCmd.InteractWorldGUI interactGui1 = (InteractWorldCmd.InteractWorldGUI) interactWorldCmd1.getGUI("main", player);
                                interactGui1.world = world.get();
                                player.openGUI(interactGui1.init());
                                interactGui1.genItems(36);
                                return;
                            }

                            player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(MainGUI.class, "invalid-world-name"));
                            player.openGUI(gui);
                        }
                );
                break;
            case 21:
                ListWorldCmd listWorldCmd = new ListWorldCmd();

                if (!player.hasPermission(listWorldCmd.getPermission())) {
                    player.sendMessage(PrefixMessage.permission_message);

                    player.getBukkitPlayer().closeInventory();

                    return;
                }

                BWGUI listWorldGui = listWorldCmd.getGUI("listworld_main",  player);

                player.openGUI(listWorldGui);

                listWorldGui.genItems(36);

                break;
            case 23:
                CreateWorldCmd createWorldCmd = new CreateWorldCmd();

                if (!player.hasPermission(createWorldCmd.getPermission())) {
                    player.sendMessage(PrefixMessage.permission_message);

                    player.getBukkitPlayer().closeInventory();

                    return;
                }

                BWGUI createWorldGui = createWorldCmd.getGUI("createworld_gui",  player);

                player.openGUI(createWorldGui);

                createWorldGui.genItems(36);
                break;
            case 25:
                Bukkit.getScheduler().runTaskAsynchronously(
                        BitsWorlds.plugin,
                        () -> {
                            player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(MainGUI.class, "insert-world-name-to-list-backups"));
                            player.getBukkitPlayer().closeInventory();

                            String input = ChatInput.askPlayer(player);
                            if (input.equals("!")) {
                                player.openGUI(gui);
                                return;
                            }

                            String worldName = WorldUtils.getValidWorldName(input);

                            if (worldName.isEmpty()) {
                                player.openGUI(gui);
                                player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(CreateWorldCmd.class, "invalid-world-name"));
                                return;
                            }

                            ListBackupCmd.ListBackupGui listBackupGui = (ListBackupCmd.ListBackupGui) new ListBackupCmd().getGUI("", player);

                            listBackupGui.filter = worldName;
                            listBackupGui.filterIgnoreCase = true;
                            listBackupGui.returnToMainGui = true;

                            player.openGUI(listBackupGui.init());
                        }
                );
                break;
            case 30:
            case 31:
            case 32:
                LogCmd logCmd = new LogCmd();

                if (!player.hasPermission(logCmd.getPermission())) {
                    player.sendMessage(PrefixMessage.permission_message);

                    player.getBukkitPlayer().closeInventory();

                    return;
                }

                BWGUI logCmdGUI;
                if (event.getSlot() == 30)
                    logCmdGUI = logCmd.getGUI("global",  player);
                else if (event.getSlot() == 31)
                    logCmdGUI = logCmd.getGUI("plugin",  player);
                else
                    logCmdGUI = logCmd.getGUI("world",  player);

                player.openGUI(logCmdGUI);

                logCmdGUI.genItems(45);
                break;
        }
    }
}
