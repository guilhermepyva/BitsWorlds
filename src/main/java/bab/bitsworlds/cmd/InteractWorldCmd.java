package bab.bitsworlds.cmd;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.ChatInput;
import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPermission;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.*;
import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import bab.bitsworlds.utils.WorldUtils;
import bab.bitsworlds.world.BWLoadedWorld;
import bab.bitsworlds.world.BWUnloadedWorld;
import bab.bitsworlds.world.BWorld;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InteractWorldCmd implements BWCommand, ImplGUI {
    @Override
    public BWPermission getPermission() {
        return BWPermission.MAINCMD_WORLD_INTERACT;
    }

    @Override
    public void run(BWCommandSender sender, Command cmd, String alias, String[] args) {

    }

    @Override
    public List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args) {
        return null;
    }

    @Override
    public BWGUI getGUI(String code, BWPlayer player) {
        return new InteractWorldGUI(
                "main_interact",
                5 * 9,
                LangCore.getClassMessage(getClass(), "gui-title").toString(),
                this,
                true,
                player
        );
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        InteractWorldGUI interactWorldGUI = (InteractWorldGUI) gui;

        switch (event.getSlot()) {
            case 36:
                if (interactWorldGUI.returnItem) {
                    ListWorldCmd listWorldCmd = new ListWorldCmd();

                    if (!player.hasPermission(listWorldCmd.getPermission())) {
                        player.sendMessage(PrefixMessage.permission_message);

                        player.getBukkitPlayer().closeInventory();

                        return;
                    }

                    BWGUI listWorldGui = listWorldCmd.getGUI("listworld_main",  player);

                    player.openGUI(listWorldGui);

                    listWorldGui.genItems(36);

                    break;
                }
                break;
            case 24:
                if (player.hasPermission(BWPermission.LOGS_SEE)) {
                    LogCmd.LogGUI logGUI = (LogCmd.LogGUI) new LogCmd().getGUI("", player);
                    BWorld world = interactWorldGUI.world;

                    if (world instanceof BWUnloadedWorld) {
                        if (((BWUnloadedWorld) world).getUUID() != null) {
                            logGUI.filter = LogCmd.Filter.WORLDUUID;
                            logGUI.worldUIDFilter = ((BWUnloadedWorld) world).getUUID();
                        }
                        else {
                            logGUI.filter = LogCmd.Filter.WORLDNAME;
                            logGUI.worldNameFilter = world.getName();
                        }
                    }
                    else {
                        logGUI.filter = LogCmd.Filter.WORLDUUID;
                        logGUI.worldUIDFilter = ((BWLoadedWorld) world).getWorld().getUID();
                    }

                    logGUI.returnItem = world;

                    player.openGUI(logGUI);

                    logGUI.genItems(45);
                }
                break;
            case 25:
                if (player.hasPermission(BWPermission.MAINCMD_BACKUP_LIST)) {
                    ListBackupCmd.ListBackupGui listBackupGui = (ListBackupCmd.ListBackupGui) new ListBackupCmd().getGUI("", player);

                    listBackupGui.filter = interactWorldGUI.world.getName();
                    listBackupGui.returnItemWorld = interactWorldGUI.world;
                    listBackupGui.returnItemFromInteractWorld = interactWorldGUI.returnItem;

                    player.openGUI(listBackupGui.init());
                }
                break;
            case 33:
                if (player.hasPermission(BWPermission.BACKUP)) {
                    player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "making-backup-message"));
                    try {
                        WorldUtils.copyWorld(
                                interactWorldGUI.world.getName(),
                                new File(BitsWorlds.plugin.getDataFolder() + "/backups/" + interactWorldGUI.world.getName() + "." + System.currentTimeMillis()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "backup-error-message"));
                    }
                    player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "maked-backup-message"));
                }
                break;
            case 34:
                if (player.hasPermission(BWPermission.DUPLICATE)) {
                    Bukkit.getScheduler().runTaskAsynchronously(
                            BitsWorlds.plugin,
                            () -> {
                                player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "duplicate-world-set-name-message"));
                                player.getBukkitPlayer().closeInventory();

                                String input = ChatInput.askPlayer(player);

                                if (input.equals("!")) {
                                    player.openGUI(interactWorldGUI);
                                    return;
                                }

                                player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "duplicating-world-message"));

                                String worldName = WorldUtils.getValidWorldName(input);
                                if (worldName.isEmpty() || new File(Bukkit.getWorldContainer() + "/" + worldName + "/").exists()) {
                                    player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(CreateWorldCmd.class, "name-set-unsucess"));
                                    player.openGUI(interactWorldGUI);
                                    return;
                                }

                                try {
                                    WorldUtils.copyWorld(interactWorldGUI.world.getName(), new File(Bukkit.getWorldContainer() + "/" + worldName));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "cant-duplicate-world-message"));
                                }

                                player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "duplicate-world-message"));
                                GUICore.updateGUI("listworld_main");
                                player.openGUI(interactWorldGUI);
                            }
                    );
                }
                break;
        }

        if (interactWorldGUI.world instanceof BWLoadedWorld)
            switch (event.getSlot()) {
                case 28:
                    if (player.hasPermission(BWPermission.SET_TIME)) {
                        TimeGui timeGui = (TimeGui) new TimeGuiHandler().getGUI("", player);
                        timeGui.world = interactWorldGUI.world;
                        player.openGUI(timeGui.init());
                    }
                    break;
                case 11:
                    if (player.hasPermission(BWPermission.SEE_GAMERULES)) {
                        GameRuleGui gameRuleGui = (GameRuleGui) new GameRuleHandler().getGUI("", player);
                        gameRuleGui.world = interactWorldGUI.world;
                        player.openGUI(gameRuleGui.init());
                    }
                    break;
                case 16:
                    if (player.hasPermission(BWPermission.UNLOAD_WITHOUT_SAVE)) {
                        player.getBukkitPlayer().closeInventory();
                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "unloading-world-message").setKey("%%s", interactWorldGUI.world.getName()));
                        Bukkit.unloadWorld(((BWLoadedWorld) interactWorldGUI.world).world, false);

                        if (Bukkit.getWorld(interactWorldGUI.world.getName()) != null) {
                            player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "world-cant-be-unloaded"));
                            player.openGUI(interactWorldGUI);
                            return;
                        }

                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "unloaded-world-message").setKey("%%s", interactWorldGUI.world.getName()));
                        interactWorldGUI.world = WorldUtils.getUnloadedWorld(interactWorldGUI.world.getName());
                        interactWorldGUI.update();
                        player.openGUI(interactWorldGUI);
                    }
                    break;
                case 15:
                    if (player.hasPermission(BWPermission.UNLOAD)) {
                        player.getBukkitPlayer().closeInventory();
                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "unloading-world-message").setKey("%%s", interactWorldGUI.world.getName()));
                        Bukkit.unloadWorld(((BWLoadedWorld) interactWorldGUI.world).world, true);

                        if (Bukkit.getWorld(interactWorldGUI.world.getName()) != null) {
                            player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "world-cant-be-unloaded"));
                            player.openGUI(interactWorldGUI);
                            return;
                        }

                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "unloaded-world-message").setKey("%%s", interactWorldGUI.world.getName()));
                        interactWorldGUI.world = WorldUtils.getUnloadedWorld(interactWorldGUI.world.getName());
                        interactWorldGUI.update();
                        player.openGUI(interactWorldGUI);
                    }
                    break;
                case 14:
                    if (player.hasPermission(BWPermission.SAVE)) {
                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "saving-world-message"));
                        ((BWLoadedWorld) interactWorldGUI.world).getWorld().save();
                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "world-saved-message"));
                    }
                    break;
                case 29:
                    if (player.hasPermission(BWPermission.SET_DIFFICULTY)) {
                        Difficulty difficulty = ((BWLoadedWorld) interactWorldGUI.world).world.getDifficulty();
                        switch (difficulty) {
                            case PEACEFUL:
                                ((BWLoadedWorld) interactWorldGUI.world).world.setDifficulty(Difficulty.EASY);
                                break;
                            case EASY:
                                ((BWLoadedWorld) interactWorldGUI.world).world.setDifficulty(Difficulty.NORMAL);
                                break;
                            case NORMAL:
                                ((BWLoadedWorld) interactWorldGUI.world).world.setDifficulty(Difficulty.HARD);
                                break;
                            case HARD:
                                ((BWLoadedWorld) interactWorldGUI.world).world.setDifficulty(Difficulty.PEACEFUL);
                                break;
                        }
                    }

                    interactWorldGUI.genItems(29);
                    break;
                case 19:
                    if (player.hasPermission(BWPermission.TELEPORT))
                        player.getBukkitPlayer().teleport(((BWLoadedWorld) interactWorldGUI.world).world.getSpawnLocation());
                    break;
                case 20:
                    if (player.hasPermission(BWPermission.TELEPORT_OTHER_PLAYER))
                        Bukkit.getScheduler().runTaskAsynchronously(
                                BitsWorlds.plugin,
                                () -> {
                                    player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "teleport-set-player-message"));
                                    player.getBukkitPlayer().closeInventory();

                                    String input = ChatInput.askPlayer(player);

                                    if (input.equals("!")) {
                                        player.openGUI(interactWorldGUI);
                                        return;
                                    }

                                    Player target = Bukkit.getPlayerExact(input);
                                    if (target == null) {
                                        player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "teleport-not-online-player-message"));
                                        player.openGUI(interactWorldGUI);
                                        return;
                                    }

                                    player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "player-teleported-message").setKey("%%pn", input));
                                    Bukkit.getScheduler().runTask(BitsWorlds.plugin, () -> target.teleport(((BWLoadedWorld) interactWorldGUI.world).world.getSpawnLocation()));
                                }
                        );
                    break;
                case 21:
                    if (player.hasPermission(BWPermission.TELEPORT_ALL_PLAYERS)) {
                        Bukkit.getOnlinePlayers().forEach(target -> target.teleport(((BWLoadedWorld) interactWorldGUI.world).world.getSpawnLocation()));
                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "teleport-all-message"));
                    }
                    break;
            }
        else
            switch (event.getSlot()) {
                case 16:
                    if (player.hasPermission(BWPermission.LOAD)) {
                        player.getBukkitPlayer().closeInventory();
                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "loading-world-message").setKey("%%s", interactWorldGUI.world.getName()));
                        World world = Bukkit.createWorld(new WorldCreator(interactWorldGUI.world.getName()));

                        if (world == null) {
                            player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "world-cant-be-loaded"));
                            player.openGUI(interactWorldGUI);
                            return;
                        }

                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "loaded-world-message").setKey("%%s", interactWorldGUI.world.getName()));
                        interactWorldGUI.world = new BWLoadedWorld(world);
                        interactWorldGUI.update();
                        player.openGUI(interactWorldGUI);
                        break;
                    }
                    case 10:
                        if (player.hasPermission(BWPermission.DELETE_WORLD)) {
                            Bukkit.getScheduler().runTaskAsynchronously(
                                    BitsWorlds.plugin,
                                    () -> {
                                        player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "delete-world-confirmation-message"));
                                        player.getBukkitPlayer().closeInventory();

                                        String input = ChatInput.askPlayer(player);

                                        boolean deleted = false;

                                        if (input.equalsIgnoreCase("y")) {
                                            player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "deleting-world"));
                                            try {
                                                FileUtils.deleteDirectory(((BWUnloadedWorld) interactWorldGUI.world).getFile());
                                            } catch (IOException e) {
                                                player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "error-deleting-message"));
                                                e.printStackTrace();
                                            }
                                            player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "deleted-world"));
                                            deleted = true;
                                        }

                                        if (interactWorldGUI.returnItem && deleted) {
                                            ListWorldCmd listWorldCmd = new ListWorldCmd();

                                            if (!player.hasPermission(listWorldCmd.getPermission())) {
                                                player.sendMessage(PrefixMessage.permission_message);

                                                player.getBukkitPlayer().closeInventory();

                                                return;
                                            }

                                            BWGUI listWorldGui = listWorldCmd.getGUI("listworld_main",  player);

                                            player.openGUI(listWorldGui);

                                            listWorldGui.genItems(36);
                                        }
                                        else if (!deleted) {
                                            player.openGUI(interactWorldGUI);
                                        }
                                    }
                            );
                        }
                        break;
                case 19:
                    if (player.hasPermission(BWPermission.RENAME_WORLD)) {
                        Bukkit.getScheduler().runTaskAsynchronously(
                                BitsWorlds.plugin,
                                () -> {
                                    player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "rename-world-set-name-message"));
                                    player.getBukkitPlayer().closeInventory();

                                    String input = ChatInput.askPlayer(player);

                                    if (input.equals("!")) {
                                        player.openGUI(interactWorldGUI);
                                        return;
                                    }

                                    player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "renaming-world-message"));

                                    String worldName = WorldUtils.getValidWorldName(input);
                                    if (worldName.isEmpty() || new File(Bukkit.getWorldContainer() + "/" + worldName + "/").exists()) {
                                        player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(CreateWorldCmd.class, "name-set-unsucess"));
                                        player.openGUI(interactWorldGUI);
                                        return;
                                    }

                                    File newFile = WorldUtils.renameWorld(((BWUnloadedWorld) interactWorldGUI.world).getFile(), worldName);

                                    if (newFile == null) {
                                        player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "cant-rename-world-message"));
                                        player.openGUI(interactWorldGUI);
                                        return;
                                    }

                                    player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "renamed-world-message").setKey("%%n", worldName));
                                    GUICore.updateGUI("listworld_main");
                                    interactWorldGUI.world = new BWUnloadedWorld(newFile);
                                    interactWorldGUI.update();
                                    player.openGUI(interactWorldGUI);
                                }
                        );
                    }
                    break;
            }
    }

    public class InteractWorldGUI extends BWGUI {
        public BWorld world;
        public BWPlayer player;
        public boolean returnItem;

        public InteractWorldGUI(String id, int size, String title, ImplGUI guiClass, boolean updatable, BWPlayer player) {
            super(id, size, title, guiClass, updatable);
            this.player = player;
            returnItem = false;
        }

        @Override
        public void setupItem(int item) {
            switch (item) {
                case 24:
                    if (player.hasPermission(BWPermission.LOGS_SEE)) {
                        this.setItem(24, new GUIItem(
                                Material.BOOK,
                                ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "log-list-item-title").toString(),
                                new ArrayList<>(),
                                LangCore.getClassMessage(InteractWorldCmd.class, "log-list-item-guide-mode"),
                                player
                        ));
                    }
                case 25:
                    if (player.hasPermission(BWPermission.MAINCMD_BACKUP_LIST)) {
                        this.setItem(25, new GUIItem(
                                Material.BOOK,
                                ChatColor.GOLD + LangCore.getClassMessage(ListBackupCmd.class, "gui-title").toString(),
                                new ArrayList<>(),
                                LangCore.getClassMessage(InteractWorldCmd.class, "backup-list-item-guide-mode"),
                                player
                        ));
                    }
                    break;
                case 33:
                    if (player.hasPermission(BWPermission.BACKUP)) {
                        this.setItem(33, new GUIItem(
                                Material.EYE_OF_ENDER,
                                ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "backup-item-title").toString(),
                                new ArrayList<>(),
                                LangCore.getClassMessage(InteractWorldCmd.class, "backup-item-guide-mode"),
                                player
                        ));
                    }
                    break;
                case 34:
                    if (player.hasPermission(BWPermission.DUPLICATE)) {
                        this.setItem(34, new GUIItem(
                                Material.ENDER_PEARL,
                                ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "duplicate-world-item-title").toString(),
                                new ArrayList<>(),
                                LangCore.getClassMessage(InteractWorldCmd.class, "duplicate-world-item-guide-mode"),
                                player
                        ));
                    }
                    break;
                case 36:
                    this.setItem(36, new GUIItem(
                            Material.SIGN,
                            ChatColor.GOLD + LangCore.getUtilMessage("back-item-title").toString(),
                            Collections.emptyList(),
                            LangCore.getUtilMessage("back-item-guide-mode"),
                            player
                    ));
                    returnItem = true;
                    break;
            }

            if (world instanceof BWLoadedWorld)
                switch (item) {
                    case 4:
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
                            description.add(ChatColor.GOLD + "UUID: " + ChatColor.WHITE + bukWorld.getUID());

                            this.setItem(4, new GUIItem(material, world.getName(), description));
                        break;
                    case 11:
                        if (player.hasPermission(BWPermission.SEE_GAMERULES)) {
                            this.setItem(11, new GUIItem(
                                    Material.ENCHANTED_BOOK,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "gamerule-item-title").toString(),
                                    new ArrayList<>(),
                                    LangCore.getClassMessage(InteractWorldCmd.class, player.hasPermission(BWPermission.SET_GAMERULE) ? "gamerule-item-guide-mode2" : "gamerule-item-guide-mode1"),
                                    player
                            ));
                        }
                        break;
                    case 16:
                        if (player.hasPermission(BWPermission.UNLOAD_WITHOUT_SAVE)) {
                            this.setItem(16, new GUIItem(
                                    Material.DIAMOND_HOE,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "unload-without-save-item-title").toString(),
                                    new ArrayList<>(GUIItem.loreJumper(
                                            LangCore.getClassMessage(InteractWorldCmd.class, "unload-without-save-item-warn").toString(),
                                            ChatColor.RED.toString(),
                                            ChatColor.RED + "" + ChatColor.BOLD + LangCore.getClassMessage(InteractWorldCmd.class, "unload-without-save-item-warn-prefix").toString()
                                    )),
                                    LangCore.getClassMessage(InteractWorldCmd.class, "unload-without-save-item-guide-mode"),
                                    player
                            ));
                        }
                        break;
                    case 15:
                        if (player.hasPermission(BWPermission.UNLOAD)) {
                            this.setItem(15, new GUIItem(
                                    Material.IRON_HOE,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "unload-item-title").toString(),
                                    new ArrayList<>(),
                                    LangCore.getClassMessage(InteractWorldCmd.class, "unload-item-guide-mode"),
                                    player
                            ));
                        }
                        break;
                    case 14:
                        if (player.hasPermission(BWPermission.SAVE)) {
                            this.setItem(14, new GUIItem(
                                    Material.IRON_AXE,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "save-world-item-tile").toString(),
                                    new ArrayList<>(),
                                    LangCore.getClassMessage(InteractWorldCmd.class, "save-world-item-guide-mode"),
                                    player
                            ));
                        }
                        break;
                    case 19:
                        if (player.hasPermission(BWPermission.TELEPORT)) {
                            this.setItem(19, new GUIItem(
                                    Material.IRON_BARDING,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "teleport-self-item-title").toString(),
                                    new ArrayList<>(),
                                    LangCore.getClassMessage(InteractWorldCmd.class, "teleport-self-guide-mode"),
                                    player
                            ));
                        }
                        break;
                    case 20:
                        if (player.hasPermission(BWPermission.TELEPORT_OTHER_PLAYER)) {
                            this.setItem(20, new GUIItem(
                                    Material.GOLD_BARDING,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "teleport-other-item-title").toString(),
                                    new ArrayList<>(),
                                    LangCore.getClassMessage(InteractWorldCmd.class, "teleport-other-guide-mode"),
                                    player
                            ));
                        }
                        break;
                    case 21:
                        if (player.hasPermission(BWPermission.TELEPORT_ALL_PLAYERS)) {
                            this.setItem(21, new GUIItem(
                                    Material.DIAMOND_BARDING,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "teleport-all-item-title").toString(),
                                    new ArrayList<>(),
                                    LangCore.getClassMessage(InteractWorldCmd.class, "teleport-all-guide-mode"),
                                    player
                            ));
                        }
                        break;
                    case 28:
                        if (player.hasPermission(BWPermission.SEE_TIME)) {
                            List<String> timeDescription = new ArrayList<>();

                            timeDescription.add("");
                            timeDescription.add(ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "actual-hour").setKey("%%h",ChatColor.WHITE + WorldUtils.getHours(((BWLoadedWorld) world).getWorld())).toString());
                            timeDescription.add(ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "actual-tick").setKey("%%t", ChatColor.WHITE + String.valueOf(((BWLoadedWorld) world).getWorld().getTime())).toString());

                            this.setItem(28, new GUIItem(
                                    Material.WATCH,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "time-item-title").toString(),
                                    timeDescription,
                                    LangCore.getClassMessage(InteractWorldCmd.class, player.hasPermission(BWPermission.SET_TIME) ? "time-guide-mode2" : "time-guide-mode1"),
                                    player
                            ));
                        }
                        break;
                    case 29:
                        if (player.hasPermission(BWPermission.SEE_DIFFICULTY)) {
                            List<String> diffucultyDesc = new ArrayList<>();

                            Difficulty worldDifficulty = ((BWLoadedWorld) world).world.getDifficulty();

                            for (Difficulty difficulty : Difficulty.values())
                                diffucultyDesc.add((worldDifficulty == difficulty ? ChatColor.AQUA : ChatColor.DARK_BLUE) + LangCore.getClassMessage(InteractWorldCmd.class,  difficulty.name().toLowerCase()).toString());

                            if (player.hasPermission(BWPermission.SET_DIFFICULTY)) {
                                diffucultyDesc.add("");
                                diffucultyDesc.add(ChatColor.WHITE + LangCore.getClassMessage(InteractWorldCmd.class, "edit-difficulty").toString());
                            }

                            this.setItem(29, new GUIItem(
                                    Material.SKULL_ITEM,
                                    1,
                                    (short) 4,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "difficulty-item-title").toString(),
                                    diffucultyDesc,
                                    LangCore.getClassMessage(InteractWorldCmd.class, player.hasPermission(BWPermission.SET_DIFFICULTY) ? "difficulty-item-guide-mode2" : "difficulty-item-guide-mode1"),
                                    player
                            ));
                        }
                        break;
                }
            else
                switch (item) {
                    case 4:
                        this.setItem(4, new GUIItem(Material.STONE, world.getName(), Arrays.asList(
                                "",
                                ChatColor.GOLD + LangCore.getClassMessage(MainGUI.class, "status-word").setKey("%%s", ChatColor.RED + LangCore.getUtilMessage("unloaded-word").toString()).toString(),
                                ChatColor.GOLD + "UUID: " + ChatColor.WHITE + ((BWUnloadedWorld) world).getUUID()
                        )));
                        break;
                    case 16:
                        if (player.hasPermission(BWPermission.LOAD)) {
                            this.setItem(16, new GUIItem(
                                    Material.GOLD_HOE,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "load-world-item-title").toString(),
                                    new ArrayList<>(),
                                    LangCore.getClassMessage(InteractWorldCmd.class, "load-world-item-guide-mode"),
                                    player
                            ));
                            break;
                        }
                    case 10:
                        if (player.hasPermission(BWPermission.DELETE_WORLD)) {
                            this.setItem(10, new GUIItem(
                                    Material.REDSTONE_BLOCK,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "delete-world-item-title").toString(),
                                    new ArrayList<>(Arrays.asList("", LangCore.getClassMessage(InteractWorldCmd.class, "delete-world-item-description").setKey("%%red", ChatColor.RED.toString()).setKey("%%white", ChatColor.WHITE.toString()).toString())),
                                    LangCore.getClassMessage(InteractWorldCmd.class, "delete-world-item-guide-mode"),
                                    player
                            ));
                        }
                        break;
                    case 19:
                        if (player.hasPermission(BWPermission.RENAME_WORLD)) {
                            this.setItem(19, new GUIItem(
                                    Material.NAME_TAG,
                                    ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "rename-world-item-title").toString(),
                                    new ArrayList<>(),
                                    LangCore.getClassMessage(InteractWorldCmd.class, "rename-world-item-guide-mode"),
                                    player
                            ));
                        }
                        break;
                }
        }

        @Override
        public BWGUI init() {
            if (world instanceof BWLoadedWorld)
                genItems(4, 11, 15, 16, 14, 25, 33, 34, 19, 20, 21, 24, 28, 27, 29);
            else

                genItems(4, 16, 24, 25, 33, 34, 10, 19);



            return this;
        }

        @Override
        public void update() {
            updateGui();

            GUICore.openGUIs.values().stream()
                    .filter(
                            gui -> {
                                if (!(gui instanceof InteractWorldGUI))
                                    return false;

                                return ((InteractWorldGUI) gui).world.getName().equals(this.world.getName());
                            }
                    ).forEach(gui -> ((InteractWorldGUI) gui).update(this.world));
        }

        public void update(BWorld world) {
            this.world = world;

            updateGui();
        }

        private void updateGui() {
            for (int i = 0; i <= 44; i++)
                this.setItem(i, new ItemStack(Material.AIR));

            init();

            if (returnItem)
                genItems(36);
        }
    }

    public class GameRuleGui extends BWGUI {
        public BWorld world;
        public BWPlayer player;
        public List<String> gamerules;

        public GameRuleGui(String id, int size, String title, ImplGUI guiClass, boolean updatable, BWPlayer player) {
            super(id, size, title, guiClass, updatable);
            this.player = player;
        }

        @Override
        public void setupItem(int item) {
            switch (item) {
                case 0:
                    gamerules = new ArrayList<>();
                    int i = 0;
                    for (String gameRule : ((BWLoadedWorld) world).world.getGameRules()) {
                        List<String> description = new ArrayList<>();
                        String value = ((BWLoadedWorld) world).world.getGameRuleValue(gameRule);

                        boolean addEffect = false;

                        if (!value.equals("true") && !value.equals("false")) {
                            description.add(ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "value-word").setKey("%%v", ChatColor.WHITE + value).toString());
                        } else {
                            boolean boolValue = Boolean.valueOf(value);

                            if (boolValue) {
                                description.add(ChatColor.AQUA + LangCore.getUtilMessage("enabled-word").toString());
                                description.add(ChatColor.DARK_BLUE + LangCore.getUtilMessage("disabled-word").toString());
                                addEffect = true;

                            } else {
                                description.add(ChatColor.DARK_BLUE + LangCore.getUtilMessage("enabled-word").toString());
                                description.add(ChatColor.AQUA + LangCore.getUtilMessage("disabled-word").toString());
                            }
                        }

                        if (player.hasPermission(BWPermission.SET_GAMERULE)) {
                            description.add("");
                            description.add(ChatColor.WHITE + LangCore.getClassMessage(InteractWorldCmd.class, "gamerule-click-to-change-value").toString());
                        }

                        GUIItem gameruleItem = new GUIItem(
                                Material.ENCHANTED_BOOK,
                                ChatColor.GOLD + gameRule,
                                description
                        );

                        if (addEffect)
                            gameruleItem.addEffect();

                        this.setItem(i, gameruleItem);
                        gamerules.add(i, gameRule);
                        i++;
                    }
                    break;
                case 36:
                    this.setItem(36, new GUIItem(
                            Material.SIGN,
                            ChatColor.GOLD + LangCore.getUtilMessage("back-item-title").toString(),
                            Collections.emptyList(),
                            LangCore.getUtilMessage("back-item-guide-mode"),
                            player
                    ));
                    break;
            }
        }

        @Override
        public BWGUI init() {
            genItems(0, 36);

            return this;
        }

        @Override
        public void update() {
            init();
        }
    }

    public class GameRuleHandler implements ImplGUI {
        @Override
        public BWGUI getGUI(String code, BWPlayer player) {
            return new GameRuleGui(
                    "interaction_gamerule",
                    5 * 9,
                    LangCore.getClassMessage(InteractWorldCmd.class, "gamerule-item-title").toString(),
                    this,
                    true,
                    player
            );
        }

        @Override
        public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
            GameRuleGui gameRuleGui = (GameRuleGui) gui;

            if (gameRuleGui.gamerules.size() - 1 >= event.getSlot() && player.hasPermission(BWPermission.SET_GAMERULE)) {
                String gamerule = gameRuleGui.gamerules.get(event.getSlot());
                String value = ((BWLoadedWorld) gameRuleGui.world).world.getGameRuleValue(gamerule);

                if (!value.equals("true") && !value.equals("false")) {
                    Bukkit.getScheduler().runTaskAsynchronously(
                            BitsWorlds.plugin,
                            () -> {
                                player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "gamerule-set-message"));
                                player.getBukkitPlayer().closeInventory();

                                String input = ChatInput.askPlayer(player);

                                if (input.equals("!")) {
                                    player.openGUI(gameRuleGui);
                                    return;
                                }

                                ((BWLoadedWorld) gameRuleGui.world).world.setGameRuleValue(gamerule, input);
                                GUICore.updateGUI("interaction_gamerule");
                                gameRuleGui.genItems(0);
                                player.openGUI(gameRuleGui);
                            }
                    );
                } else {
                    boolean boolValue = Boolean.valueOf(value);
                    ((BWLoadedWorld) gameRuleGui.world).world.setGameRuleValue(gamerule, String.valueOf(!boolValue));
                    GUICore.updateGUI("interaction_gamerule");
                    gameRuleGui.genItems(0);
                }
            }

            else if (event.getSlot() == 36) {
                InteractWorldCmd interactWorldCmd = new InteractWorldCmd();
                InteractWorldCmd.InteractWorldGUI interactGui = (InteractWorldCmd.InteractWorldGUI) interactWorldCmd.getGUI("main", player);
                interactGui.world = gameRuleGui.world;
                player.openGUI(interactGui.init());
                interactGui.genItems(36);
            }
        }
    }

    public class TimeGui extends BWGUI {
        public BWorld world;
        public BWPlayer player;

        public TimeGui(String id, int size, String title, ImplGUI guiClass, boolean updatable, BWPlayer player) {
            super(id, size, title, guiClass, updatable);
            this.player = player;
        }

        @Override
        public void setupItem(int item) {
            switch (item) {
                case 11:
                    this.setItem(11, new GUIItem(
                            Material.STAINED_CLAY,
                            1,
                            (short) 1,
                            ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "sunrise").toString(),
                            new ArrayList<>()
                    ));
                case 12:
                    this.setItem(12, new GUIItem(
                            Material.STAINED_CLAY,
                            1,
                            (short) 4,
                            ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "noon").toString(),
                            new ArrayList<>()
                    ));
                case 13:
                    this.setItem(13, new GUIItem(
                            Material.STAINED_CLAY,
                            1,
                            (short) 2,
                            ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "sunset").toString(),
                            new ArrayList<>()
                    ));
                case 14:
                    this.setItem(14, new GUIItem(
                            Material.STAINED_CLAY,
                            1,
                            (short) 11,
                            ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "midnight").toString(),
                            new ArrayList<>()
                    ));
                case 15:
                    this.setItem(15, new GUIItem(
                            Material.NAME_TAG,
                            ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "personalized").toString(),
                            new ArrayList<>()
                    ));
                case 18:
                    this.setItem(18, new GUIItem(
                            Material.SIGN,
                            ChatColor.GOLD + LangCore.getUtilMessage("back-item-title").toString(),
                            Collections.emptyList(),
                            LangCore.getUtilMessage("back-item-guide-mode"),
                            player
                    ));
                    break;
            }
        }

        @Override
        public BWGUI init() {
            genItems(11, 12, 13, 14, 15, 18);

            return this;
        }

        @Override
        public void update() {
            init();
        }
    }

    public class TimeGuiHandler implements ImplGUI {
        @Override
        public BWGUI getGUI(String code, BWPlayer player) {
            return new TimeGui(
                    "interaction_time",
                    3 * 9,
                    LangCore.getClassMessage(InteractWorldCmd.class, "time-item-title").toString(),
                    this,
                    true,
                    player
            );
        }

        @Override
        public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
            TimeGui timeGui = (TimeGui) gui;

            switch (event.getSlot()) {
                case 11:
                case 12:
                case 13:
                case 14:
                    long ticks;
                    String message;
                    if (event.getSlot() == 11) {
                        ticks = 0;
                        message = "defined-to-sunrise";
                    }
                    else if (event.getSlot() == 12) {
                        ticks = 6000;
                        message = "defined-to-noon";
                    }
                    else if (event.getSlot() == 13) {
                        ticks = 12000;
                        message = "defined-to-sunset";
                    }
                    else {
                        ticks = 18000;
                        message = "defined-to-midnight";
                    }

                    ((BWLoadedWorld) timeGui.world).world.setTime(ticks);
                    player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, message));
                    break;
                case 15:
                    Bukkit.getScheduler().runTaskAsynchronously(
                            BitsWorlds.plugin,
                            () -> {
                                player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "time-set-message"));
                                player.getBukkitPlayer().closeInventory();

                                String input = ChatInput.askPlayer(player);

                                if (input.equals("!")) {
                                    player.openGUI(timeGui);
                                    return;
                                }

                                Long time;
                                try {
                                    time = Long.parseLong(input);
                                } catch (NumberFormatException e) {
                                    player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "time-set-unsucess"));
                                    time = null;
                                }

                                if (time != null) {
                                    ((BWLoadedWorld) timeGui.world).getWorld().setTime(time);
                                    player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(InteractWorldCmd.class, "defined-to-personalized").setKey("%%t", String.valueOf(time)));
                                }
                                player.openGUI(timeGui.init());
                            }
                    );
                    break;
                case 18:
                    InteractWorldCmd interactWorldCmd = new InteractWorldCmd();
                    InteractWorldCmd.InteractWorldGUI interactGui = (InteractWorldCmd.InteractWorldGUI) interactWorldCmd.getGUI("main", player);
                    interactGui.world = timeGui.world;
                    player.openGUI(interactGui.init());
                    interactGui.genItems(36);
                    break;
            }
        }
    }

    static String actualHours = ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "actual-hour").setKey("%%h","").toString();
    static String actualTick = ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "actual-tick").setKey("%%t", "").toString();;

    public static void timeUpdater() {
        Lang actualLang = LangCore.lang;

        Bukkit.getScheduler().runTaskTimer(
                BitsWorlds.plugin,
                () -> {
                    if (LangCore.lang != actualLang) {
                        actualHours = ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "actual-hour").setKey("%%h","").toString();
                        actualTick = ChatColor.GOLD + LangCore.getClassMessage(InteractWorldCmd.class, "actual-tick").setKey("%%t", "").toString();
                    }

                    GUICore.openGUIs.forEach(
                            (bwPlayer, bwGui) -> {
                                if (bwGui instanceof InteractWorldCmd.InteractWorldGUI && ((InteractWorldGUI) bwGui).world instanceof BWLoadedWorld && bwPlayer.hasPermission(BWPermission.SEE_TIME)) {
                                    ItemStack item = bwGui.getItem(28);
                                    ItemMeta meta = item.getItemMeta();
                                    List<String> lore = meta.getLore();

                                    lore.set(1, actualHours + ChatColor.WHITE + WorldUtils.getHours(((BWLoadedWorld) ((InteractWorldGUI) bwGui).world).getWorld()));
                                    lore.set(2, actualTick + ChatColor.WHITE + ((BWLoadedWorld) ((InteractWorldGUI) bwGui).world).getWorld().getTime());

                                    meta.setLore(lore);
                                    item.setItemMeta(meta);
                                }
                            }
                    );
                },
                0,
                36
        );
    }
}
