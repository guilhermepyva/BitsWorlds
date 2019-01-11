package bab.bitsworlds.gui;

import bab.bitsworlds.extensions.BWPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public interface ImplGUI {
    Map<Integer, BWGUI> getGUIs();

    void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui);
}