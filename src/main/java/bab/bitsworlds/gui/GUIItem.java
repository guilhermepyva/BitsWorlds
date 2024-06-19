package bab.bitsworlds.gui;

import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.multilanguage.LangCore;
import bab.bitsworlds.multilanguage.MLMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIItem extends ItemStack {
    public GUIItem(Material material) {
        super(material);
    }

    public GUIItem(Material material, int amout, short damage) {
        super(material, amout, damage);
    }

    public GUIItem(Material material, String title, List<String> description) {
        super(material);

        ItemMeta meta = this.getItemMeta();

        meta.setLore(description);
        meta.setDisplayName(title);

        this.setItemMeta(meta);
    }

    public GUIItem(Material material, int amout, short damage, String title, List<String> description) {
        super(material, amout, damage);

        ItemMeta meta = this.getItemMeta();

        meta.setLore(description);
        meta.setDisplayName(title);

        this.setItemMeta(meta);
    }

    public GUIItem(Material material, String title, List<String> description, MLMessage guideModeMessage, BWPlayer player) {
        this(material, title, description);

        ItemMeta meta = this.getItemMeta();

        meta.setLore(addGuideLore(guideModeMessage, player, new ArrayList<>()));

        this.setItemMeta(meta);
    }

    public GUIItem(Material material, int amount, short damage, String title, List<String> description, MLMessage guideModeMessage, BWPlayer player) {
        this(material, amount, damage, title, description);

        ItemMeta meta = this.getItemMeta();

        meta.setLore(addGuideLore(guideModeMessage, player, new ArrayList<>()));

        this.setItemMeta(meta);
    }

    public void addEffect() {
        ItemMeta meta = this.getItemMeta();

        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        this.setItemMeta(meta);
    }

    public static List<String> addGuideLore(MLMessage message, BWPlayer player, List<String> list) {
        if (GUICore.guideMode(player)) {
            list.add("");
            list.addAll(loreJumper(message.toString(), ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC, ChatColor.BOLD + LangCore.getClassMessage(GUICore.class, "guide-mode-words").toString() + ": "));
        }
        return list;
    }

    public static List<String> loreJumper(String string, String prefixColor, String prefixWord) {
        return loreJumper(string, 27, prefixColor, prefixWord);
    }

    public static List<String> loreJumper(String string, int chars, String prefixColor, String prefix) {
        List<String> list = new ArrayList<>();

        String[] splitted = string.split(" ");

        int c = 0, i = 0;

        StringBuilder sb = new StringBuilder();
        boolean insertPrefix = true;
        for (String word : splitted) {
            if (insertPrefix) {
                sb.append(prefix);
                c = c + prefix.length();
                sb.append(prefixColor);
                insertPrefix = false;
            }

            if (word.equals("\n")) {
                if (sb.toString().equals(prefix))
                    list.add(sb.toString());
                else
                    list.add("");

                sb = new StringBuilder();
                sb.append(prefixColor);
                c = 0;
                i++;
                continue;
            }

            sb.append(word);
            c = c + word.length();

            i++;

            if (splitted.length == i) {
                list.add(sb.toString());
                continue;
            }

            sb.append(" ");

            if (c >= chars) {
                list.add(sb.toString());
                sb = new StringBuilder();
                sb.append(prefixColor);
                c = 0;
            }
        }

        return list;
    }
}
