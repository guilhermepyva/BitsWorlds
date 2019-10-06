package bab.bitsworlds.gui;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.SkullCore;
import bab.bitsworlds.cmd.ConfigCmd;
import bab.bitsworlds.cmd.LogCmd;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
                            case 19:
                                this.setItem(19, new GUIItem(
                                        Material.REDSTONE,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "config-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "config-item-guide-mode").setKey("%%file", ChatColor.ITALIC + "config.yml"),
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
                        }
                    }

                    @Override
                    public void update() {
                        init();
                    }

                    @Override
                    public BWGUI init() {
                        genItems(4, 8, 19, 30);

                        return this;
                    }
                };
        }

        throw new NullPointerException("No GUI with id " + code + " found");
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        switch (event.getSlot()) {
            case 8:
                GUICore.alternateGuideMode(player);

                gui.init();
                break;
            case 19:
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
            case 30:
                LogCmd logCmd = new LogCmd();

                if (!player.hasPermission(logCmd.getPermission())) {
                    player.sendMessage(PrefixMessage.permission_message);

                    player.getBukkitPlayer().closeInventory();

                    return;
                }

                BWGUI logCmdGUI = logCmd.getGUI("global",  player);

                player.openGUI(logCmdGUI);

                logCmdGUI.genItems(45);
        }
    }
}
