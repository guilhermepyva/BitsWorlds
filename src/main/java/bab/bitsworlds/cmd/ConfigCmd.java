package bab.bitsworlds.cmd;

import bab.bitsworlds.cmd.impl.BWCommand;
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
import org.bukkit.inventory.meta.ItemMeta;

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
                            case 27:
                                //BACK ITEM
                                /*ItemStack backItem = new ItemStack(Material.SIGN);

                                ItemMeta backItemMeta = backItem.getItemMeta();

                                backItemMeta.setDisplayName(ChatColor.GOLD + LangCore.getClassMessage(ConfigCmd.class, "back-item-title").toString());

                                List<String> backItemLore = new ArrayList<>();

                                GUICore.addGuideLore(LangCore.getClassMessage(ConfigCmd.class, "back-item-guide-mode"), player, backItemLore);

                                backItemMeta.setLore(backItemLore);

                                backItem.setItemMeta(backItemMeta);

                                this.setItem(27, backItem);*/

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
                        genItems(0);

                        return this;
                    }
                }.init();
        }

        throw new NullPointerException("No GUI with id " + code + " found");
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        switch (event.getSlot()) {
            case 0:
                BWTaskResponse response = new BWConfigTask(BWConfigTask.ConfigTask.LanguageSet, LangCore.lang.ordinal() + 2 > Lang.values().length ? Lang.values()[0] : Lang.values()[LangCore.lang.ordinal() + 1], player.getBukkitPlayer().getUniqueId()).execute();

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
        /*ItemStack countryBanner = getCountryBanner(LangCore.lang);
        ItemMeta countryBannerMeta = countryBanner.getItemMeta();

        countryBannerMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + LangCore.lang.title);
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

        GUICore.addGuideLore(LangCore.getClassMessage(ConfigCmd.class, "language-config-guide-mode"), player, countryBannerLore);

        countryBannerMeta.setLore(countryBannerLore);
        countryBanner.setItemMeta(countryBannerMeta);

        gui.setItem(0, countryBanner);*/

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
            list = Arrays.asList("language");
        }

        else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("language")) {
                list = Arrays.asList("EN", "PT", "SP", "FR");
            }
        }

        return list;
    }
}