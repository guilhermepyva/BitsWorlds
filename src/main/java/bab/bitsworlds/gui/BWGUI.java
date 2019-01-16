package bab.bitsworlds.gui;

import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventoryCustom;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to work with Bukkit windows
 */
public abstract class BWGUI extends CraftInventoryCustom {

    public String id;
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

    public BWGUI(String id, int size, String title, ImplGUI guiClass) {
        super(null, size, title);
        this.properties = new HashMap<>();
        this.guiClass = guiClass;
        this.id = id;

        for (Property property : Property.values()) {
            properties.put(property, property.defaultProperty);
        }

        init();
    }

    public void setProperty(Property property, Object value) {
        properties.put(property, value);
    }

    public abstract void update();

    public abstract BWGUI init();

    public ImplGUI getGUIClass() {
        return guiClass;
    }
}