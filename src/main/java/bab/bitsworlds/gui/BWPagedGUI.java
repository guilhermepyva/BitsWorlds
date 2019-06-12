package bab.bitsworlds.gui;

public abstract class BWPagedGUI extends BWGUI {
    public int actualPage;
    public int lastPage;

    public Object itemsID;

    public BWPagedGUI(String id, int size, String title, ImplGUI guiClass, boolean updatable) {
        super(id, size, title, guiClass, updatable);
    }
}
