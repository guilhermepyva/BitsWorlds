package bab.bitsworlds.cmd;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.ChatInput;
import bab.bitsworlds.SkullCore;
import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPermission;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.*;
import bab.bitsworlds.logger.LogAction;
import bab.bitsworlds.logger.LogCore;
import bab.bitsworlds.logger.LogRecorder;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import bab.bitsworlds.utils.WorldUtils;
import bab.bitsworlds.world.BWLoadedWorld;
import bab.bitsworlds.world.WorldCreator;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CreateWorldCmd implements BWCommand, ImplGUI {
    @Override
    public BWPermission getPermission() {
        return BWPermission.MAINCMD_WORLD_CREATE;
    }

    @Override
    public void run(BWCommandSender sender, Command cmd, String alias, String[] args) {
        if (sender instanceof BWPlayer) {
            ((BWPlayer) sender).openGUI(getGUI("createworld_gui", (BWPlayer) sender));
        }
    }

    @Override
    public List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args) {
        return null;
    }

    @Override
    public BWGUI getGUI(String code, BWPlayer player) {
        if ("createworld_gui".equals(code)) {
            return new CreateWorldGUI(
                    "createworld_gui",
                    5 * 9,
                    LangCore.getClassMessage(getClass(), "gui-title").toString(),
                    this,
                    false,
                    player
            ).init();
        }

        throw new NullPointerException("No GUI with id " + code + " found");
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        CreateWorldGUI createWorldGUI = (CreateWorldGUI) gui;

        switch (event.getSlot()) {
            case 4:
                if (!createWorldGUI.creator.isComplete())
                    return;

                org.bukkit.WorldCreator bukCreator = new org.bukkit.WorldCreator(createWorldGUI.creator.name);
                bukCreator.environment(createWorldGUI.creator.environment);
                if (createWorldGUI.creator.worldType == WorldType.VOID)
                    bukCreator.type(org.bukkit.WorldType.FLAT);
                else
                    bukCreator.type(org.bukkit.WorldType.valueOf(createWorldGUI.creator.worldType.name()));
                bukCreator.generateStructures(createWorldGUI.creator.generateStructures);
                if (createWorldGUI.creator.seed != null)
                    bukCreator.seed(createWorldGUI.creator.seed);
                if (createWorldGUI.creator.worldType == WorldType.VOID)
                    bukCreator.generator(new ChunkGenerator() {
                        @Override
                        public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
                            return new byte[world.getMaxHeight() / 16][];
                        }
                    });

                player.closeInventory();
                player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(CreateWorldCmd.class, "creating-world-message"));

                World world = bukCreator.createWorld();
                world.save();

                LogCore.addLog(LogAction.WORLD_CREATED, null, new LogRecorder(player.getBukkitPlayer().getUniqueId()), new Timestamp(System.currentTimeMillis()), world.getUID(), world.getName());

                GUICore.updateGUI("listworld_main");

                if (player.hasPermission(BWPermission.MAINCMD_WORLD_INTERACT)) {
                    InteractWorldCmd.InteractWorldGUI worldGUI = (InteractWorldCmd.InteractWorldGUI) new InteractWorldCmd().getGUI("", player);
                    worldGUI.world = new BWLoadedWorld(world);
                    player.openGUI(worldGUI.init());
                }

                player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(CreateWorldCmd.class, "world-created-message"));
            case 9:
            case 10:
            case 11:
                if (event.getSlot() == 9)
                    createWorldGUI.creator.environment = World.Environment.NORMAL;
                else if (event.getSlot() == 10)
                    createWorldGUI.creator.environment = World.Environment.NETHER;
                else
                    createWorldGUI.creator.environment = World.Environment.THE_END;

                createWorldGUI.genItems(9, 10, 11, 4);

                break;
            case 15:
                Bukkit.getScheduler().runTaskAsynchronously(
                        BitsWorlds.plugin,
                        () -> {
                            WorldCreator creator = createWorldGUI.creator;

                            player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(CreateWorldCmd.class, "seed-set-message"));
                            player.closeInventory();

                            String input = ChatInput.askPlayer(player);

                            if (input.equals("!")) {
                                player.openGUI(createWorldGUI);
                                return;
                            }

                            Long seed;
                            try {
                                seed = Long.parseLong(input);
                            } catch (NumberFormatException e) {
                                player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(CreateWorldCmd.class, "seed-set-unsucess"));
                                seed = null;
                            }

                            if (seed != null) {
                                creator.seed = seed;
                                createWorldGUI.genItems(15);
                            }
                            player.openGUI(createWorldGUI);
                        }
                );
                break;
            case 16:
                createWorldGUI.creator.generateStructures = !createWorldGUI.creator.generateStructures;
                createWorldGUI.genItems(16);
                break;
            case 17:
                Bukkit.getScheduler().runTaskAsynchronously(
                        BitsWorlds.plugin,
                        () -> {
                            WorldCreator creator = createWorldGUI.creator;

                            player.sendMessage(PrefixMessage.info.getPrefix(), LangCore.getClassMessage(CreateWorldCmd.class, "name-set-message"));
                            player.closeInventory();

                            String input = ChatInput.askPlayer(player);

                            if (input.equals("!")) {
                                player.openGUI(createWorldGUI);
                                return;
                            }

                            String worldName = WorldUtils.getValidWorldName(input);

                            if (worldName.isEmpty() || new File(Bukkit.getWorldContainer() + "/" + worldName + "/").exists()) {
                                player.openGUI(createWorldGUI);
                                player.sendMessage(PrefixMessage.error.getPrefix(), LangCore.getClassMessage(CreateWorldCmd.class, "name-set-unsucess"));
                                return;
                            }

                            creator.name = worldName;
                            createWorldGUI.genItems(4, 17);
                            player.openGUI(createWorldGUI);
                        }
                );
                break;
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
                if (event.getSlot() == 29)
                    createWorldGUI.creator.worldType = WorldType.NORMAL;
                else if (event.getSlot() == 30)
                    createWorldGUI.creator.worldType = WorldType.FLAT;
                else if (event.getSlot() == 31)
                    createWorldGUI.creator.worldType = WorldType.LARGE_BIOMES;
                else if (event.getSlot() == 32)
                    createWorldGUI.creator.worldType = WorldType.AMPLIFIED;
                else if (event.getSlot() == 33)
                    createWorldGUI.creator.worldType = WorldType.VOID;

                createWorldGUI.genItems(4, 29, 30, 31, 32, 33);

                break;
            case 36:
                if (createWorldGUI.getItem(36) != null)
                    player.openGUI(new MainGUI().getGUI("main", player));
                break;
        }
    }

    private class CreateWorldGUI extends BWGUI {
        public BWPlayer player;
        public WorldCreator creator;

        public CreateWorldGUI(String id, int size, String title, ImplGUI guiClass, boolean updatable, BWPlayer player) {
            super(id, size, title, guiClass, updatable);
            this.player = player;
            this.creator = new WorldCreator();
            this.creator.generateStructures = true;
        }

        @Override
        public void setupItem(int item) {
            switch (item) {
                case 4:
                    List<String> description = new ArrayList<>();

                    if (!creator.isComplete()) {
                        description.add(ChatColor.RED + LangCore.getClassMessage(CreateWorldCmd.class, "missing").toString());

                        if (creator.name == null)
                            description.add(ChatColor.RED + "  • " + LangCore.getClassMessage(CreateWorldCmd.class, "missing-name").toString());
                        if (creator.environment == null)
                            description.add(ChatColor.RED + "  • " + LangCore.getClassMessage(CreateWorldCmd.class, "missing-environment").toString());
                        if (creator.worldType == null)
                            description.add(ChatColor.RED + "  • " + LangCore.getClassMessage(CreateWorldCmd.class, "missing-world-type").toString());
                    }
                    else
                        description.add(ChatColor.WHITE + LangCore.getClassMessage(CreateWorldCmd.class, "all-done").toString());

                    GUIItem createWorldItem = new GUIItem(
                            Material.SKULL_ITEM,
                            1,
                            (short) 3,
                            ChatColor.GOLD + LangCore.getClassMessage(CreateWorldCmd.class, "gui-title").toString(),
                            description
                    );
                    SkullMeta createWorldItemItemMeta = (SkullMeta) createWorldItem.getItemMeta();
                    SkullCore.applyToSkull(createWorldItemItemMeta, SkullCore.Skull.CREATEWORLDICON);
                    createWorldItem.setItemMeta(createWorldItemItemMeta);
                    this.setItem(4, createWorldItem);
                    break;
                case 9:
                    GUIItem overworlditem = new GUIItem(
                            Material.GRASS,
                            ChatColor.GOLD + LangCore.getUtilMessage("overworld").toString(),
                            new ArrayList<>(),
                            LangCore.getClassMessage(CreateWorldCmd.class, "dimension-guide-mode"),
                            player
                    );

                    if (creator.environment != null && creator.environment == World.Environment.NORMAL)
                        overworlditem.addEffect();

                    this.setItem(9, overworlditem);
                    break;
                case 10:
                    GUIItem netheritem = new GUIItem(
                            Material.NETHERRACK,
                            ChatColor.GOLD + LangCore.getUtilMessage("nether").toString(),
                            new ArrayList<>(),
                            LangCore.getClassMessage(CreateWorldCmd.class, "dimension-guide-mode"),
                            player
                    );

                    if (creator.environment != null && creator.environment == World.Environment.NETHER)
                        netheritem.addEffect();

                    this.setItem(10, netheritem);
                    break;
                case 11:
                    GUIItem theenditem = new GUIItem(
                            Material.ENDER_STONE,
                            ChatColor.GOLD + LangCore.getUtilMessage("theend").toString(),
                            new ArrayList<>(),
                            LangCore.getClassMessage(CreateWorldCmd.class, "dimension-guide-mode"),
                            player
                    );

                    if (creator.environment != null && creator.environment == World.Environment.THE_END)
                        theenditem.addEffect();

                    this.setItem(11, theenditem);
                    break;
                case 15:
                    Material material;
                    List<String> seedDescription = new ArrayList<>();

                    if (creator.seed != null) {
                        material = Material.MAP;
                        seedDescription.add(ChatColor.WHITE + creator.seed.toString());
                    }
                    else
                        material = Material.EMPTY_MAP;

                    GUIItem seedItem = new GUIItem(
                            material,
                            ChatColor.GOLD + LangCore.getClassMessage(CreateWorldCmd.class, "seed-item-title").toString(),
                            seedDescription,
                            LangCore.getClassMessage(CreateWorldCmd.class, "seed-item-guide-mode"),
                            player
                    );

                    if (creator.seed != null)
                        seedItem.addEffect();

                    this.setItem(15, seedItem);
                    break;
                case 16:
                    List<String> doorDescription = new ArrayList<>();

                    if (creator.generateStructures)
                        doorDescription.add(ChatColor.WHITE + LangCore.getUtilMessage("enabled-word").toString());
                    else {
                        doorDescription.add(ChatColor.WHITE + LangCore.getUtilMessage("disabled-word").toString());
                    }

                    GUIItem doorItem = new GUIItem(
                            Material.WOOD_DOOR,
                            ChatColor.GOLD + LangCore.getClassMessage(CreateWorldCmd.class, "generate-structures-item-title").toString(),
                            doorDescription,
                            LangCore.getClassMessage(CreateWorldCmd.class, "generate-structures-item-guide-mode"),
                            player
                    );

                    if (creator.generateStructures)
                        doorItem.addEffect();

                    this.setItem(16, doorItem);
                    break;
                case 17:
                    List<String> nameDescription = new ArrayList<>();

                    if (creator.name != null)
                        nameDescription.add(ChatColor.WHITE + creator.name);

                    this.setItem(17, new GUIItem(
                            Material.NAME_TAG,
                            ChatColor.GOLD + LangCore.getClassMessage(CreateWorldCmd.class, "name-item-title").toString(),
                            nameDescription,
                            LangCore.getClassMessage(CreateWorldCmd.class, "name-item-guide-mode"),
                            player
                    ));
                    break;
                case 29:
                    GUIItem normalItem = new GUIItem(
                            Material.LONG_GRASS,
                            1,
                            (short) 1,
                            ChatColor.GOLD + LangCore.getClassMessage(CreateWorldCmd.class, "normal-type").toString(),
                            new ArrayList<>(),
                            LangCore.getClassMessage(CreateWorldCmd.class, "type-item-guide"),
                            player
                    );

                    if (creator.worldType != null && creator.worldType == WorldType.NORMAL)
                        normalItem.addEffect();

                    this.setItem(29, normalItem);
                    break;
                case 30:
                    GUIItem flatItem = new GUIItem(
                            Material.DIRT,
                            ChatColor.GOLD + LangCore.getClassMessage(CreateWorldCmd.class, "flat-type").toString(),
                            new ArrayList<>(),
                            LangCore.getClassMessage(CreateWorldCmd.class, "type-item-guide"),
                            player
                    );

                    if (creator.worldType != null && creator.worldType == WorldType.FLAT)
                        flatItem.addEffect();

                    this.setItem(30, flatItem);
                    break;
                case 31:
                    GUIItem largeBiomesItem = new GUIItem(
                            Material.LEAVES,
                            ChatColor.GOLD + LangCore.getClassMessage(CreateWorldCmd.class, "large-biomes-type").toString(),
                            new ArrayList<>(),
                            LangCore.getClassMessage(CreateWorldCmd.class, "type-item-guide"),
                            player
                    );

                    if (creator.worldType != null && creator.worldType == WorldType.LARGE_BIOMES)
                        largeBiomesItem.addEffect();

                    this.setItem(31, largeBiomesItem);
                    break;
                case 32:
                    GUIItem amplifiedItem = new GUIItem(
                            Material.RED_ROSE,
                            ChatColor.GOLD + LangCore.getClassMessage(CreateWorldCmd.class, "amplified-type").toString(),
                            new ArrayList<>(),
                            LangCore.getClassMessage(CreateWorldCmd.class, "type-item-guide"),
                            player
                    );

                    if (creator.worldType != null && creator.worldType == WorldType.AMPLIFIED)
                        amplifiedItem.addEffect();

                    this.setItem(32, amplifiedItem);
                    break;
                case 33:
                    GUIItem customizedItem = new GUIItem(
                            Material.GLASS,
                            ChatColor.GOLD + LangCore.getClassMessage(CreateWorldCmd.class, "void-type").toString(),
                            new ArrayList<>(),
                            LangCore.getClassMessage(CreateWorldCmd.class, "type-item-guide"),
                            player
                    );

                    if (creator.worldType != null && creator.worldType == WorldType.VOID)
                        customizedItem.addEffect();

                    this.setItem(33, customizedItem);
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
            genItems(4, 9, 10, 11, 15, 16, 17, 29, 30, 31, 32, 33);

            return this;
        }

        @Override
        public void update() {
            init();
        }
    }

    public enum WorldType {
        NORMAL, FLAT, LARGE_BIOMES, AMPLIFIED, VOID;
    }
}
