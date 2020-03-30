package bab.bitsworlds.logger;

import org.bukkit.Material;

public enum LogAction {
    GLOBAL_CONFIG_LANGUAGESET(Material.BANNER),
    GLOBAL_CONFIG_DATABASETYPESET(Material.ANVIL),
    GLOBAL_CONFIG_NOTESSET(Material.PAPER),

    WORLD_CREATED(Material.REDSTONE_TORCH_ON);

    public Material material;

    LogAction(Material material) {
        this.material = material;
    }
}