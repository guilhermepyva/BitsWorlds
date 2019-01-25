package bab.bitsworlds.cmd;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.db.BWSQL;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.*;
import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.PrefixMessage;
import bab.bitsworlds.task.BWTask;
import bab.bitsworlds.task.BWTaskResponse;
import bab.bitsworlds.task.tasks.BWConfigTask;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.*;

public class ConfigCmd implements BWCommand, ImplGUI {

    @Override
    public String getPermission() {
        return "bitsworlds.maincmd.config";
    }

    public void run(BWCommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            if (!(commandSender instanceof BWPlayer)) {
                commandSender.sendMessage(
                        PrefixMessage.error.getPrefix(),
                        LangCore.getClassMessage(BitsWorldsCmd.class, "cmdsender-cant-run-cmd")
                );
                return;
            }

            BWPlayer player = (BWPlayer) commandSender;

            player.openGUI(getGUI("config_main", player));
            return;
        }

        if (strings[1].equalsIgnoreCase("language")) {
            if (strings.length == 2) {
                commandSender.sendMessage(PrefixMessage.warn.getPrefix(),
                        LangCore.getClassMessage(getClass(), "language-config-use")
                        .setKey("%%cmd", ChatColor.BOLD + "/BitsWorlds" + ChatColor.ITALIC + " language <EN|PT|SP|FR>"));
                return;
            }

            Lang lang;

            try {
                lang = Lang.valueOf(strings[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                commandSender.sendMessage(PrefixMessage.error.getPrefix(),
                        LangCore.getClassMessage(getClass(), "language-config-invalid-arg")
                        .setKey("%%arg", ChatColor.ITALIC + strings[2])
                        .setKey("%%args", "<EN|PT|SP|FR>")
                        .setKey("%%prefixColor", PrefixMessage.error.getDefaultChatColor().toString()));
                return;
            }

            BWTaskResponse response = new BWConfigTask(BWConfigTask.ConfigTask.LanguageSet, lang, commandSender instanceof BWPlayer ? ((BWPlayer) commandSender).getBukkitPlayer().getUniqueId() : null).execute();

            if (response.getCode() == 0 && response instanceof BWTask.BWExceptionResponse) {
                commandSender.reportExceptionResponse((BWTask.BWExceptionResponse) response);
                return;
            }

            if (response.getCode() == 1) {
                commandSender.sendMessage(PrefixMessage.warn.getPrefix(),
                        LangCore.getClassMessage(getClass(), "language-config-already")
                        .setKey("%%lang", ChatColor.ITALIC + LangCore.lang.name())
                        .setKey("%%prefixColor", PrefixMessage.warn.getDefaultChatColor().toString()));
                return;
            }

            commandSender.sendMessage(PrefixMessage.info.getPrefix(),
                    LangCore.getClassMessage(this.getClass(), "language-updated").setKey("%%lang", ChatColor.BOLD + LangCore.lang.title));
        }

        else if (strings[1].equalsIgnoreCase("db")) {
            if (strings.length == 2) {
                commandSender.sendMessage(PrefixMessage.warn.getPrefix(),
                        LangCore.getClassMessage(getClass(), "config-use")
                                .setKey("%%cmd", ChatColor.BOLD + "/BitsWorlds" + ChatColor.ITALIC + " config db <MYSQL|SQLITE>"));
                return;
            }

            boolean sqlite;

            if (strings[2].equalsIgnoreCase("MYSQL"))
                sqlite = false;
            else if (strings[2].equalsIgnoreCase("SQLITE"))
                sqlite = true;
            else {
                commandSender.sendMessage(PrefixMessage.error.getPrefix(),
                        LangCore.getClassMessage(getClass(), "invalid-arg")
                                .setKey("%%arg", ChatColor.ITALIC + strings[2])
                                .setKey("%%args", "<MYSQL|SQLITE>")
                                .setKey("%%prefixColor", PrefixMessage.error.getDefaultChatColor().toString()));
                return;
            }

            BWTaskResponse response = new BWConfigTask(BWConfigTask.ConfigTask.DatabaseTypeSet, sqlite, commandSender instanceof BWPlayer ? ((BWPlayer) commandSender).getBukkitPlayer().getUniqueId() : null).execute();

            if (response.getCode() == 0 && response instanceof BWTask.BWExceptionResponse) {
                commandSender.reportExceptionResponse((BWTask.BWExceptionResponse) response);
                return;
            }

            String dbTypeString = BitsWorlds.plugin.getConfig().getString("db").equalsIgnoreCase("sqlite") ? "SQLite" : "MySQL";

            if (response.getCode() == 1) {
                commandSender.sendMessage(PrefixMessage.warn.getPrefix(),
                        LangCore.getClassMessage(getClass(), "database-config-already")
                                .setKey("%%db", ChatColor.ITALIC + dbTypeString)
                                .setKey("%%prefixColor", PrefixMessage.warn.getDefaultChatColor().toString()));
                return;
            }

            commandSender.sendMessage(PrefixMessage.info.getPrefix(),
                    LangCore.getClassMessage(this.getClass(), "database-updated").setKey("%%db", ChatColor.BOLD + dbTypeString));
        }
    }

    public BWGUI getGUI(String code, BWPlayer player) {

        switch (code) {
            case "config_main":
                return new BWGUI(
                        "config_main",
                        4*9,
                        ChatColor.DARK_AQUA + LangCore.getClassMessage(this.getClass(), "gui-title").setKey("%%name", "BitsWorlds").toString(),
                        this
                ) {
                    @Override
                    public void setupItem(int item) {
                        switch (item) {
                            case 0:
                                updateCountryBannerItem(this, player);
                                break;
                            case 1:
                                List<String> databaseLore = new ArrayList<>();

                                if (BWSQL.sqlite) {
                                    databaseLore.add(ChatColor.AQUA + "SQLite");
                                    databaseLore.add(ChatColor.BLUE + "MySQL");
                                }
                                else {
                                    databaseLore.add(ChatColor.BLUE + "SQLite");
                                    databaseLore.add(ChatColor.AQUA + "MySQL");
                                }

                                databaseLore.add("");
                                databaseLore.addAll(GUIItem.loreJumper(
                                        LangCore.getClassMessage(ConfigCmd.class, "database-item-lore").setKey("%%file", ChatColor.ITALIC + "config.yml").toString(),
                                        ChatColor.WHITE.toString(),
                                        ChatColor.WHITE + "" + ChatColor.BOLD + LangCore.getUtilMessage("warn-word").toString().toUpperCase() + ": "
                                ));

                                if (BitsWorlds.plugin.getConfig().getString("db").equalsIgnoreCase("sqlite") != BWSQL.sqlite) {
                                    databaseLore.add("");
                                    databaseLore.add(ChatColor.YELLOW + LangCore.getClassMessage(ConfigCmd.class, "database-item-need-restart").toString());
                                }

                                this.setItem(1, new GUIItem(
                                        Material.ANVIL,
                                        ChatColor.AQUA + "" + ChatColor.BOLD + LangCore.getClassMessage(ConfigCmd.class, "database-item-title").toString(),
                                        databaseLore,
                                        LangCore.getClassMessage(ConfigCmd.class, "database-config-guide-mode"),
                                        player
                                ));

                                break;
                            case 27:
                                this.setItem(27, new GUIItem(
                                        Material.SIGN,
                                        ChatColor.GOLD + LangCore.getClassMessage(ConfigCmd.class, "back-item-title").toString(),
                                        Collections.emptyList(),
                                        LangCore.getClassMessage(ConfigCmd.class, "back-item-guide-mode"),
                                        player
                                ));

                                break;
                        }
                    }

                    @Override
                    public BWGUI init() {
                        genItems(0, 1);

                        return this;
                    }
                }.init();
        }

        throw new NullPointerException("No GUI with id " + code + " found");
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        BWTaskResponse response;

        switch (event.getSlot()) {
            case 0:
                response = new BWConfigTask(BWConfigTask.ConfigTask.LanguageSet, LangCore.lang.ordinal() + 2 > Lang.values().length ? Lang.values()[0] : Lang.values()[LangCore.lang.ordinal() + 1], player.getBukkitPlayer().getUniqueId()).execute();

                if (response.getCode() == 0 && response instanceof BWTask.BWExceptionResponse) {
                    player.reportExceptionResponse((BWTask.BWExceptionResponse) response);
                    return;
                }

                GUICore.updateAllGUIs();

                if (gui.getItem(27) != null) {
                    GUICore.openGUIs.get(player).genItems(27);
                }

                player.sendMessage(PrefixMessage.info.getPrefix(),
                        LangCore.getClassMessage(this.getClass(), "language-updated").setKey("%%lang", ChatColor.BOLD + LangCore.lang.title));

                break;
            case 1:
                response = new BWConfigTask(BWConfigTask.ConfigTask.DatabaseTypeSet, !BitsWorlds.plugin.getConfig().getString("db").equalsIgnoreCase("sqlite"), player.getBukkitPlayer().getUniqueId()).execute();

                if (response.getCode() == 0 && response instanceof BWTask.BWExceptionResponse) {
                    player.reportExceptionResponse((BWTask.BWExceptionResponse) response);
                    return;
                }

                GUICore.updateAllGUIs();

                if (gui.getItem(27) != null) {
                    GUICore.openGUIs.get(player).genItems(27);
                }

                player.sendMessage(PrefixMessage.info.getPrefix(),
                        LangCore.getClassMessage(this.getClass(), "database-updated").setKey("%%db", ChatColor.BOLD + (BitsWorlds.plugin.getConfig().getString("db").equalsIgnoreCase("sqlite") ? "SQLite" : "MySQL")));
            case 27:
                if (gui.getItem(27) != null) {
                    player.openGUI(new MainGUI().getGUI("main", player));
                }
        }
    }

    public ItemStack setCountryBanner(Lang lang, GUIItem item) {
        BannerMeta bannerMeta = (BannerMeta) item.getItemMeta();

        switch (lang) {
            case EN:
                bannerMeta.setPatterns(Arrays.asList(
                        new Pattern(DyeColor.RED, PatternType.BASE),
                        new Pattern(DyeColor.WHITE, PatternType.STRIPE_SMALL),
                        new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_LEFT)
                ));
                break;
            case PT:
                bannerMeta.setPatterns(Arrays.asList(
                        new Pattern(DyeColor.GREEN, PatternType.BASE),
                        new Pattern(DyeColor.YELLOW, PatternType.RHOMBUS_MIDDLE),
                        new Pattern(DyeColor.YELLOW, PatternType.RHOMBUS_MIDDLE),
                        new Pattern(DyeColor.YELLOW, PatternType.RHOMBUS_MIDDLE),
                        new Pattern(DyeColor.BLUE, PatternType.CIRCLE_MIDDLE),
                        new Pattern(DyeColor.BLUE, PatternType.CIRCLE_MIDDLE)
                ));
                break;
            case FR:
                bannerMeta.setPatterns(Arrays.asList(
                        new Pattern(DyeColor.WHITE, PatternType.BASE),
                        new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM),
                        new Pattern(DyeColor.RED, PatternType.STRIPE_TOP)
                ));
                break;
            case SP:
                bannerMeta.setPatterns(Arrays.asList(
                        new Pattern(DyeColor.YELLOW, PatternType.BASE),
                        new Pattern(DyeColor.RED, PatternType.STRIPE_LEFT),
                        new Pattern(DyeColor.RED, PatternType.STRIPE_RIGHT),
                        new Pattern(DyeColor.RED, PatternType.FLOWER),
                        new Pattern(DyeColor.RED, PatternType.STRIPE_LEFT)
                ));
        }

        bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        item.setItemMeta(bannerMeta);
        return item;
    }

    private void updateCountryBannerItem(BWGUI gui, BWPlayer player) {
        List<String> countryBannerLore = new ArrayList<>();

        for (Lang lang: Lang.values()) {
            StringBuilder sb = new StringBuilder();

            if (lang == LangCore.lang)
                sb.append(ChatColor.AQUA);
            else
                sb.append(ChatColor.BLUE);

            sb.append(lang.title);
            countryBannerLore.add(sb.toString());
        }

        gui.setItem(0, setCountryBanner(LangCore.lang, new GUIItem(
                Material.BANNER,
                ChatColor.AQUA + "" + ChatColor.BOLD + LangCore.lang.title,
                countryBannerLore,
                LangCore.getClassMessage(ConfigCmd.class, "language-config-guide-mode"),
                player
        )));
    }

    @Override
    public List<String> tabComplete(BWCommandSender sender, Command cmd, String alias, String[] args) {
        List<String> list = null;

        if (args.length == 2) {
            list = Arrays.asList("language", "db");
        }

        else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("language")) {
                list = Arrays.asList("EN", "PT", "SP", "FR");
            }

            else if (args[1].equalsIgnoreCase("db")) {
                list = Arrays.asList("MYSQL", "SQLITE");
            }
        }

        return list;
    }
}