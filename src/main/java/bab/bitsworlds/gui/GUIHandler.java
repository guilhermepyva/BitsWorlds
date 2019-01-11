package bab.bitsworlds.gui;

import bab.bitsworlds.extensions.BWPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIHandler implements Listener {

    public static List<ImplGUI> listeners;
    public static Map<BWPlayer, BWGUI> openGUIs;

    public static void init() {
        listeners = new ArrayList<>();
        openGUIs = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!check(e.getWhoClicked()))
            return;

        BWPlayer player = new BWPlayer((Player) e.getWhoClicked());

        BWGUI gui = GUIHandler.openGUIs.get(player);

        if ((Boolean) gui.properties.get(BWGUI.Property.CLICK_SOUND)) {
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

        GUIHandler.openGUIs.remove(new BWPlayer((Player) e.getPlayer()));
    }

    private boolean check(HumanEntity whoClicked) {
        return whoClicked instanceof Player && GUIHandler.openGUIs.containsKey(new BWPlayer((Player) whoClicked));
    }
}