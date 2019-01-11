package bab.bitsworlds.gui;

import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventoryCustom;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to work with Bukkit windows
 */
public class BWGUI extends CraftInventoryCustom {

    public Map<Property, Object> properties;
    private ImplGUI guiClass;

    public enum Property {
        CLICK_SOUND(true),
        STATUE_ITEMS(true);

        public Object defaultProperty;
        Property(Object defaultProperty) {
            this.defaultProperty = defaultProperty;
        }
    }

    public BWGUI(int size, String title, ImplGUI guiClass) {
        super(null, size, title);
        this.properties = new HashMap<>();
        this.guiClass = guiClass;

        for (Property property : Property.values()) {
            properties.put(property, property.defaultProperty);
        }
    }

    public void setProperty(Property property, Object value) {
        properties.put(property, value);
    }

    public ImplGUI getGUIClass() {
        return guiClass;
    }
}