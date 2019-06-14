package bab.bitsworlds.logger;

import org.bukkit.Material;

public enum LogAction {
    GLOBAL_CONFIG_LANGUAGESET(Material.BANNER),
    GLOBAL_CONFIG_DATABASETYPESET(Material.ANVIL);

    public Material material;

    LogAction(Material material) {
        this.material = material;
    }
}