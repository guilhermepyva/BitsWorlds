package bab.bitsworlds.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public interface ImplGUI {
    Map<Integer, BWGUI> getGUIs();

    void clickEvent(InventoryClickEvent event);
}