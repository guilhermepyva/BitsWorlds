package bab.bitsworlds.gui;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventoryCustom;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to work with Bukkit windows
 */
public abstract class BWGUI extends CraftInventoryCustom implements Cloneable {

    public String id;
    public Map<Property, Object> properties;
    private ImplGUI guiClass;
    public boolean updatable;

    public enum Property {
        CLICK_SOUND(true),
        STATUE_ITEMS(true);

        public Object defaultProperty;
        Property(Object defaultProperty) {
            this.defaultProperty = defaultProperty;
        }
    }

    public BWGUI(String id, int size, String title, ImplGUI guiClass, boolean updatable) {
        super(null, size, ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + title);
        this.properties = new HashMap<>();
        this.guiClass = guiClass;
        this.id = id;
        this.updatable = updatable;

        for (Property property : Property.values()) {
            properties.put(property, property.defaultProperty);
        }

        init();
    }

    public void setProperty(Property property, Object value) {
        properties.put(property, value);
    }

    public abstract void setupItem(int item);

    public void genItems(int... items) {
        for (int item : items) {
            setupItem(item);
        }
    }

    public abstract BWGUI init();

    public abstract void update();

    public ImplGUI getGUIClass() {
        return guiClass;
    }

    @Override
    protected BWGUI clone() {
        try {
            return (BWGUI) super.clone();
        } catch(CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}