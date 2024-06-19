package bab.bitsworlds.gui;

import bab.bitsworlds.extensions.BWPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import java.util.*;

public class GUICore implements Listener {

    public static HashMap<BWPlayer, BWGUI> openGUIs = new HashMap<>();

    public static List<BWPlayer> guideMode = new ArrayList<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!check(e.getWhoClicked()))
            return;

        if (e.getClickedInventory() == null || e.getClickedInventory().getType() == null || e.getAction() == null) {
            e.setCancelled(true);
            return;
        }
        if (e.getClickedInventory().getType() == InventoryType.PLAYER && e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            e.setCancelled(true);
            return;
        }
        if (e.getClickedInventory().getType() == InventoryType.PLAYER)
            return;

        BWPlayer player = new BWPlayer((Player) e.getWhoClicked());

        BWGUI gui = GUICore.openGUIs.get(player);

        if ((Boolean) gui.properties.get(BWGUI.Property.CLICK_SOUND)) {
            try {
                player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.25F);
            } catch (NoSuchFieldError error) {
                player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1F, 1.25F);
            }
        }
        if ((Boolean) gui.properties.get(BWGUI.Property.STATUE_ITEMS))
            e.setCancelled(true);

        gui.getGUIClass().clickEvent(e, player, gui);
    }

    @EventHandler
    public void onInventoryInteract(InventoryDragEvent e) {
        if (!check(e.getWhoClicked()))
            return;

        e.setCancelled(true);
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
        new ArrayList<>(openGUIs.keySet()).stream().filter(bwPlayer -> openGUIs.get(bwPlayer).id.equals(id)).forEach(bwPlayer ->  openGUIs.get(bwPlayer).update());
    }

    public static void updateAllGUIs() {
        new ArrayList<>(openGUIs.keySet()).forEach(bwPlayer ->  openGUIs.get(bwPlayer).update());
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