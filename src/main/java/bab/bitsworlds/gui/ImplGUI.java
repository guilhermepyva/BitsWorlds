package bab.bitsworlds.gui;

import bab.bitsworlds.extensions.BWPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public interface ImplGUI {
    BWGUI getGUI(String code, BWPlayer player);

    void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui);
}