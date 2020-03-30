package bab.bitsworlds.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class BWPagedGUI<T> extends BWGUI {
    public int actualPage;
    public int lastPage;

    public T itemsID;

    public BWPagedGUI(String id, int size, String title, ImplGUI guiClass, boolean updatable) {
        super(id, size, title, guiClass, updatable);
    }

    public void setupItemPage(int nextItem, int previousItem) {
        setItem(previousItem, new ItemStack(Material.AIR));
        setItem(nextItem, new ItemStack(Material.AIR));

        if (actualPage > 0) {
            setupItem(previousItem);
        }
        if (actualPage < lastPage) {
            setupItem(nextItem);
        }
    }
}
