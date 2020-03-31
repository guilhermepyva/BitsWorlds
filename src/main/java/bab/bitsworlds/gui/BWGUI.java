package bab.bitsworlds.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to work with Bukkit windows
 */
public abstract class BWGUI implements Cloneable {
    public Inventory inventory;
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
        inventory = Bukkit.createInventory(null, size, title);
        this.properties = new HashMap<>();
        this.guiClass = guiClass;
        this.id = id;
        this.updatable = updatable;

        for (Property property : Property.values()) {
            properties.put(property, property.defaultProperty);
        }
    }

    public void setItem(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }

    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
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