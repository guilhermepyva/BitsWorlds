package bab.bitsworlds.cmd;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.cmd.impl.BWCommand;
import bab.bitsworlds.extensions.BWCommandSender;
import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.gui.BWGUI;
import bab.bitsworlds.gui.ImplGUI;
import bab.bitsworlds.multilanguage.Lang;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.LangMessage;
import bab.bitsworlds.multilanguage.PrefixMessage;
import bab.bitsworlds.task.BWTask;
import bab.bitsworlds.task.BWTaskResponse;
import bab.bitsworlds.task.responses.DefaultResponse;
import bab.bitsworlds.task.tasks.BWConfigTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
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
                        LangCore.getClassMessage(this.getClass(), "cmdsender-cant-run-cmd")
                );
                return;
            }

            BWPlayer player = (BWPlayer) commandSender;

            player.openGUI(getGUIs().get(0));
            return;
        }

        if (strings[1].equalsIgnoreCase("language")) {
            if (strings.length == 2) {
                commandSender.sendMessage(PrefixMessage.warn.getPrefix(),
                        LangCore.getClassMessage(getClass(), "language-config-use")
                        .setKey("%%cmd", ChatColor.BOLD + "/BitsWorlds" + ChatColor.ITALIC)
                        .setKey("%%args", "<EN|PT|SP|FR>"));
                return;
            }

            Lang lang;

            try {
                lang = Lang.valueOf(strings[2]);
            } catch (IllegalArgumentException e) {
                commandSender.sendMessage(PrefixMessage.error.getPrefix(),
                        LangCore.getClassMessage(getClass(), "language-config-invalid-arg")
                        .setKey("%%arg", ChatColor.ITALIC + strings[2])
                        .setKey("%%args", "<EN|PT|SP|FR>")
                        .setKey("%%prefixColor", PrefixMessage.error.getDefaultChatColor().toString()));
                return;
            }

            BWTaskResponse response = new BWConfigTask(BWConfigTask.ConfigTask.LanguageSet, lang).execute();

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

    public Map<Integer, BWGUI> getGUIs() {
        Map<Integer, BWGUI> guis = new HashMap<>();

        BWGUI mainGui = new BWGUI(
                4*9,
                ChatColor.AQUA + LangCore.getClassMessage(this.getClass(), "gui-title").setKey("%%name", "BitsWorlds").getTranslatedMessage().message,
                this
        );

        updateCountryBannerItem(mainGui);

        guis.put(0, mainGui);

        return guis;
    }

    @Override
    public void clickEvent(InventoryClickEvent event, BWPlayer player, BWGUI gui) {
        switch (event.getSlot()) {
            case 0:
                BWTaskResponse response = new BWConfigTask(BWConfigTask.ConfigTask.LanguageSet, LangCore.lang.ordinal() + 2 > Lang.values().length ? Lang.values()[0] : Lang.values()[LangCore.lang.ordinal() + 1]).execute();

                if (response.getCode() == 0 && response instanceof BWTask.BWExceptionResponse) {
                    player.reportExceptionResponse((BWTask.BWExceptionResponse) response);
                    return;
                }

                player.openGUI(getGUIs().get(0));

                player.sendMessage(PrefixMessage.info.getPrefix(),
                        LangCore.getClassMessage(this.getClass(), "language-updated").setKey("%%lang", ChatColor.BOLD + LangCore.lang.title));

                break;
        }
    }

    public ItemStack getCountryBanner(Lang lang) {
        ItemStack banner = new ItemStack(Material.BANNER);
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

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

        banner.setItemMeta(bannerMeta);
        return banner;
    }

    private void updateCountryBannerItem(BWGUI gui) {
        ItemStack countryBanner = getCountryBanner(LangCore.lang);
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

        countryBannerMeta.setLore(countryBannerLore);
        countryBanner.setItemMeta(countryBannerMeta);

        gui.setItem(0, countryBanner);
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