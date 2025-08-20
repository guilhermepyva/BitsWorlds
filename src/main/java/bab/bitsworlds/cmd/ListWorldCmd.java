package bab.bitsworlds.cmd;

import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.*;
import bab.bitsworlds.gui.*;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.utils.WorldUtils;
import bab.bitsworlds.world.BWLoadedWorld;
import bab.bitsworlds.world.BWorld;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
            ).init();
        }
        throw new NullPointerException("No GUI with id " + code + " found");
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        ListWorldGUI worldListGui = (ListWorldGUI) gui;

        if (event.getSlot() <= 35 && (worldListGui.itemsID.size() - 1 >= event.getSlot() && worldListGui.itemsID.get(event.getSlot()) != null) && player.hasPermission(BWPermission.MAINCMD_WORLD_INTERACT)) {
            InteractWorldCmd interactWorldCmd = new InteractWorldCmd();
            InteractWorldCmd.InteractWorldGUI interactGui = (InteractWorldCmd.InteractWorldGUI) interactWorldCmd.getGUI("main", player);
            interactGui.world = worldListGui.itemsID.get(event.getSlot());
            player.openGUI(interactGui.init());
            interactGui.genItems(36);
        }

        switch (event.getSlot()) {
            case 36:
                if (worldListGui.getItem(36) != null)
                    player.openGUI(new MainGUI().getGUI("main", player));
                break;
            case 39:
                if (worldListGui.actualPage > 0) {
                    worldListGui.actualPage--;
                    worldListGui.setupItemPage(41, 39);
                    worldListGui.setupItem(0);
                }
                break;
            case 41:
                if (worldListGui.actualPage < worldListGui.lastPage) {
                    worldListGui.actualPage++;
                    worldListGui.setupItemPage(41, 39);
                    worldListGui.setupItem(0);
                }
                break;
            case 43:
                if (worldListGui.filter == ListWorldGUI.Filter.LOADEDWORLDS)
                    worldListGui.filter = null;
                else
                    worldListGui.filter = ListWorldGUI.Filter.LOADEDWORLDS;
                worldListGui.init();
                break;
            case 44:
                if (worldListGui.filter == ListWorldGUI.Filter.UNLOADEDWORLDS)
                    worldListGui.filter = null;
                else
                    worldListGui.filter = ListWorldGUI.Filter.UNLOADEDWORLDS;
                worldListGui.init();
                break;
        }
    }

    private static class ListWorldGUI extends BWPagedGUI<List<BWorld>>{
        public BWPlayer player;
        public Filter filter;

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
                    for (int i1 = 0; i1 < 36; i1++) {
                        setItem(i1, new ItemStack(Material.AIR));
                    }
                    for (BWorld world : queryWorlds(actualPage * 36)) {
                        if (world instanceof BWLoadedWorld) {
                            World bukWorld = ((BWLoadedWorld) world).getWorld();
                            List<String> description = new ArrayList<>();
                            Material material = Material.SHORT_GRASS;

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
                                    material = Material.LEGACY_ENDER_STONE;
                                    break;
                            }

                            description.add(ChatColor.GOLD + LangCore.getClassMessage(ListWorldCmd.class, "players-in").setKey("%%c", ChatColor.WHITE + String.valueOf(bukWorld.getPlayers().size())).toString());
                            description.add(ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "status-word").setKey("%%s", ChatColor.GREEN + LangCore.getUtilMessage("loaded-word").toString()).toString());

                            this.setItem(i, new GUIItem(material, world.getName(), description, LangCore.getClassMessage(ListWorldCmd.class, "world-item-guide-mode"), player));
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
                    break;
                case 36:
                    this.setItem(36, new GUIItem(
                            Material.LEGACY_SIGN,
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
                case 44:
                    GUIItem unloadedWorldFilter = new GUIItem(
                            Material.HOPPER,
                            ChatColor.DARK_PURPLE + LangCore.getClassMessage(ListWorldCmd.class, "unloaded-worlds-filter-item-title").toString(),
                            new ArrayList<>()
                    );

                    if (filter == Filter.UNLOADEDWORLDS)
                        unloadedWorldFilter.addEffect();

                    this.setItem(44, unloadedWorldFilter);
                    break;
                case 43:
                    GUIItem loadedWorldFilter = new GUIItem(
                            Material.HOPPER,
                            ChatColor.DARK_PURPLE + LangCore.getClassMessage(ListWorldCmd.class, "loaded-worlds-filter-item-title").toString(),
                            new ArrayList<>()
                    );

                    if (filter == Filter.LOADEDWORLDS)
                        loadedWorldFilter.addEffect();

                    this.setItem(43, loadedWorldFilter);
                    break;
            }
        }

        @Override
        public BWGUI init() {
            genItems(0, 43, 44);
            this.actualPage = 0;
            this.lastPage = calculateLastPage();

            this.setupItemPage(41, 39);

            return this;
        }

        @Override
        public void update() {
            this.lastPage = calculateLastPage();
            init();

            this.setupItemPage(41, 39);
        }

        int calculateLastPage() {
            return countWorldsByFilter() / 36;
        }

        List<BWorld> queryWorlds(int offset) {
            List<BWorld> worlds = new ArrayList<>();
            int i = 0;
            for (BWorld world : getWorldsByFilter()) {
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

        List<BWorld> getWorldsByFilter() {
            if (filter == null)
                return WorldUtils.getWorlds();
            else if (filter == Filter.LOADEDWORLDS)
                return WorldUtils.getLoadedWorlds();
            else
                return WorldUtils.getUnloadedWorlds();
        }

        int countWorldsByFilter() {
            if (filter == null)
                return WorldUtils.countWorlds();
            else if (filter == Filter.LOADEDWORLDS)
                return Bukkit.getWorlds().size();
            else
                return WorldUtils.countUnloadedWorlds();
        }

        public enum Filter {
            LOADEDWORLDS,
            UNLOADEDWORLDS
        }
    }
}
