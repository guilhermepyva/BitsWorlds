package bab.bitsworlds.gui;

import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.MLMessage;
import bab.bitsworlds.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.*;

public class GUICore implements Listener {

    public static HashMap<BWPlayer, BWGUI> openGUIs = new HashMap<>();

    public static List<BWPlayer> guideMode = new ArrayList<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!check(e.getWhoClicked()))
            return;

        BWPlayer player = new BWPlayer((Player) e.getWhoClicked());

        BWGUI gui = GUICore.openGUIs.get(player);

        if ((Boolean) gui.properties.get(BWGUI.Property.CLICK_SOUND) && e.getCurrentItem().getType() == Material.AIR) {
            player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.CLICK, 1F, 1.25F);
        }
        if ((Boolean) gui.properties.get(BWGUI.Property.STATUE_ITEMS)) {
            e.setCancelled(true);
        }

        gui.getGUIClass().clickEvent(e, player, gui);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!check(e.getPlayer()))
            return;

        GUICore.openGUIs.remove(new BWPlayer((Player) e.getPlayer()));
    }

    private boolean check(HumanEntity whoClicked) {
        return whoClicked instanceof Player && GUICore.openGUIs.containsKey(new BWPlayer((Player) whoClicked));
    }

    public static void updateGUI(String id) {
        Map<BWPlayer, BWGUI> playersToUpdate = new HashMap<>();

        openGUIs.keySet().stream().filter(bwPlayer -> openGUIs.get(bwPlayer).id.equals(id)).forEach(bwPlayer ->  playersToUpdate.put(bwPlayer, openGUIs.get(bwPlayer)));

        playersToUpdate.keySet().forEach(bwPlayer -> {
            BWGUI bwGui = openGUIs.get(bwPlayer);

            if (bwGui.updatable)
                bwPlayer.openGUI(bwGui.getGUIClass().getGUI(bwGui.id, bwPlayer));
        });
    }

    public static void updateAllGUIs() {
        Map<BWPlayer, BWGUI> playersToUpdate = new HashMap<>();

        openGUIs.keySet().forEach(bwPlayer ->  playersToUpdate.put(bwPlayer, openGUIs.get(bwPlayer)));

        playersToUpdate.keySet().forEach(bwPlayer -> {
            BWGUI bwGui = openGUIs.get(bwPlayer);

            if (bwGui.updatable)
                bwPlayer.openGUI(bwGui.getGUIClass().getGUI(bwGui.id, bwPlayer));
        });
    }

    public static void updateGUIItem(String id, int... items) {
        openGUIs.values().forEach(gui -> {
            if (gui.id.equals(id))
                gui.genItems(items);
        });
    }

    public static boolean guideMode(BWPlayer player) {
        return guideMode.contains(player);
    }

    public static void alternateGuideMode(BWPlayer player) {
        if (guideMode.contains(player))
            guideMode.remove(player);
        else
            guideMode.add(player);
    }
}