package bab.bitsworlds.gui;

import bab.bitsworlds.extensions.BWPlayer;
import bab.bitsworlds.multilanguage.MLMessage;
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

    public GUIItem(Material material, String title, List<String> description) {
        super(material);

        ItemMeta meta = this.getItemMeta();

        meta.setLore(description);
        meta.setDisplayName(title);

        this.setItemMeta(meta);
    }

    public GUIItem(Material material, String title, List<String> description, MLMessage guideModeMessage, BWPlayer player) {
        this(material, title, description);

        ItemMeta meta = this.getItemMeta();

        if (meta.getLore() == null)
            meta.setLore(new ArrayList<>());
        meta.setLore(GUICore.addGuideLore(guideModeMessage, player, meta.getLore()));

        this.setItemMeta(meta);
    }

    public void addEffect() {
        ItemMeta meta = this.getItemMeta();

        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        this.setItemMeta(meta);
    }
}
