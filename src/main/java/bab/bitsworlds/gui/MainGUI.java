package bab.bitsworlds.gui;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.cmd.ConfigCmd;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MainGUI implements ImplGUI {
    @Override
    public BWGUI getGUI(String code, BWPlayer player) {
        switch (code) {
            case "main":
                return new BWGUI(
                        "main",
                        4*9,
                        ChatColor.DARK_AQUA  + "BitsWorlds",
                        this) {
                    @Override
                    public void setupItem(int item) {
                        switch (item) {
                            case 4:
                                this.setItem(4, new GUIItem(
                                        Material.GRASS,
                                        ChatColor.GREEN + "" + ChatColor.BOLD + "BitsWorlds",
                                        Arrays.asList(ChatColor.WHITE + LangCore.getClassMessage(MainGUI.class, "version-word").setKey("%%v", BitsWorlds.plugin.getDescription().getVersion()).toString(),
                                                "",
                                                ChatColor.WHITE + "" + ChatColor.ITALIC + LangCore.getClassMessage(MainGUI.class, "by-word").setKey("%%n", ChatColor.BLUE + "MrPiva").toString()),
                                        LangCore.getClassMessage(MainGUI.class, "head-item-guide-mode"),
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
                            case 19:
                                this.setItem(19, new GUIItem(
                                        Material.BOOK_AND_QUILL,
                                        ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "config-item-title").toString(),
                                        new ArrayList<>(),
                                        LangCore.getClassMessage(MainGUI.class, "config-item-guide-mode").setKey("%%file", ChatColor.ITALIC + "config.yml"),
                                        player
                                ));

                                break;
                        }
                    }

                    @Override
                    public BWGUI init() {
                        genItems(4, 8, 19);

                        return this;
                    }
                }.init();
        }

        throw new NullPointerException("No GUI with id " + code + " found");
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        switch (event.getSlot()) {
            case 8:
                GUICore.alternateGuideMode(player);

                gui.genItems(4, 8, 19);
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
        }
    }
}
