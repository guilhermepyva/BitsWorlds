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
                //Define the new Lang
                LangCore.lang = LangCore.lang.ordinal() + 2 > Lang.values().length ? Lang.values()[0] : Lang.values()[LangCore.lang.ordinal() + 1];
                BitsWorlds.plugin.getConfig().set("language", LangCore.lang.name());
                BitsWorlds.plugin.saveConfig();

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
}