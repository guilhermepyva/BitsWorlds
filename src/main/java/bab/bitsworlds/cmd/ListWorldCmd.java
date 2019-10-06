package bab.bitsworlds.cmd;

import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.db.SQLDataManager;
import bab.bitsworlds.extensions.*;
import bab.bitsworlds.gui.*;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.utils.WorldUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

public class ListWorldCmd implements BWCommand, ImplGUI {
    @Override
    public BWPermission getPermission() {
        return BWPermission.MAINCMD_WORLD_LIST;
    }

    @Override
    public void run(BWCommandSender sender, Command cmd, String alias, String[] args) {
        if (sender instanceof BWPlayer) {
            ((BWPlayer) sender).openGUI(getGUI("listworld_main", (BWPlayer) sender));
        }
    }

    @Override
    public List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args) {
        return null;
    }

    @Override
    public BWGUI getGUI(String code, BWPlayer player) {
        if ("listworld_main".equals(code)) {
            return new ListWorldGUI(
                    "listworld_main",
                    5 * 9,
                    LangCore.getClassMessage(this.getClass(), "gui-title").toString(),
                    this,
                    true,
                    player
            );
        }
        throw new NullPointerException("No GUI with id " + code + " found");
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        BWPagedGUI<List<Integer>> pagedGUI = (BWPagedGUI) gui;

        switch (event.getSlot()) {
            case 36:
                if (gui.getItem(36) != null)
                    player.openGUI(new MainGUI().getGUI("main", player));
            case 39:
                if (pagedGUI.actualPage > 0) {
                    pagedGUI.actualPage--;
                    pagedGUI.setupItemPage(41, 39);
                    pagedGUI.setupItem(0);
                }
                break;
            case 41:
                if (pagedGUI.actualPage < pagedGUI.lastPage) {
                    pagedGUI.actualPage++;
                    pagedGUI.setupItemPage(41, 39);
                    pagedGUI.setupItem(0);
                }
                break;
        }
    }

    private static class ListWorldGUI extends BWPagedGUI<List<BWorld>> {
        public BWPlayer player;

        public ListWorldGUI(String id, int size, String title, ImplGUI guiClass, boolean updatable, BWPlayer player) {
            super(id, size, title, guiClass, updatable);
            this.player = player;
        }

        @Override
        public void setupItem(int item) {
            switch (item) {
                case 0:
                    this.itemsID = new ArrayList<>();
                    int i = 0;
                    //TODO NAO ESQUECER DE SALVAR O MUNDO LOGO APÓS ELE SER CRIADO
                    //Bukkit.createWorld(new WorldCreator("teste"));
                    //Bukkit.getWorld("teste").save();
                    for (int i1 = 0; i1 < 36; i1++) {
                        setItem(i1, new ItemStack(Material.AIR));
                    }
                    for (BWorld world : queryWorlds(actualPage * 36)) {
                        if (world instanceof BWLoadedWorld) {
                            World bukWorld = ((BWLoadedWorld) world).getWorld();
                            List<String> description = new ArrayList<>();
                            Material material = Material.GRASS;

                            String dimensionWord = LangCore.getUtilMessage("dimension-word").toString();

                            description.add("");

                            switch (bukWorld.getEnvironment()) {
                                case NORMAL:
                                    description.add(ChatColor.GOLD + dimensionWord + ": " + ChatColor.WHITE + LangCore.getUtilMessage("overworld").toString());
                                    break;
                                case NETHER:
                                    description.add(ChatColor.GOLD + dimensionWord + ": " + ChatColor.WHITE + LangCore.getUtilMessage("nether").toString());
                                    material = Material.NETHERRACK;
                                    break;
                                case THE_END:
                                    description.add(ChatColor.GOLD + dimensionWord + ": " + ChatColor.WHITE + LangCore.getUtilMessage("theend").toString());
                                    material = Material.ENDER_STONE;
                                    break;
                            }

                            description.add(ChatColor.GOLD + LangCore.getClassMessage(ListWorldCmd.class, "players-in").setKey("%%c", ChatColor.WHITE + String.valueOf(bukWorld.getPlayers().size())).toString());
                            description.add(ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "status-word").setKey("%%s", ChatColor.GREEN + LangCore.getUtilMessage("loaded-word").toString()).toString());

                            this.setItem(i, new GUIItem(material, world.getName(), description));
                        }
                        else {
                            this.setItem(i, new GUIItem(Material.STONE, world.getName(), Arrays.asList(
                                    "",
                                    ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "status-word").setKey("%%s", ChatColor.RED + LangCore.getUtilMessage("unloaded-word").toString()).toString()
                            )));
                        }

                        this.itemsID.add(i, world);
                        i++;
                    }
                case 36:
                    this.setItem(36, new GUIItem(
                            Material.SIGN,
                            ChatColor.GOLD + LangCore.getUtilMessage("back-item-title").toString(),
                            Collections.emptyList(),
                            LangCore.getUtilMessage("back-item-guide-mode"),
                            player
                    ));
                    break;

                case 39:
                    this.setItem(39, new GUIItem(
                            Material.ARROW,
                            ChatColor.GOLD.toString() + LangCore.getUtilMessage("page").toString() + " " + (this.actualPage),
                            Collections.emptyList()
                    ));
                    break;
                case 41:
                    this.setItem(41, new GUIItem(
                            Material.ARROW,
                            ChatColor.GOLD.toString() + LangCore.getUtilMessage("page").toString() + " " + (this.actualPage + 2),
                            Collections.emptyList()
                    ));
                    break;
            }
        }

        @Override
        public BWGUI init() {
            genItems(0);
            this.actualPage = 0;
            this.lastPage = calculateLastPage();
            System.out.println(WorldUtils.countWorlds() / 36);

            this.setupItemPage(41, 39);

            return this;
        }

        @Override
        public void update() {
            this.lastPage = calculateLastPage();
            genItems(0);

            this.setupItemPage(41, 39);
        }

        int calculateLastPage() {
            return WorldUtils.countWorlds() / 36;
        }

        List<BWorld> queryWorlds(int offset) {
            List<BWorld> worlds = new ArrayList<>();
            int i = 0;
            for (BWorld world : WorldUtils.getWorlds()) {
                if (i >= offset) {
                    if ((i - offset) > 35) {
                        break;
                    }
                    worlds.add(world);
                }

                i++;
            }

            return worlds;
        }
    }
}
