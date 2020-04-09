package bab.bitsworlds.world;

import bab.bitsworlds.BitsWorlds;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class BWorldPropertiesListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void worldInit(WorldInitEvent event) {
        if (!BWorldProperties.propertiesCache.containsKey(event.getWorld().getUID()))
            BWorldProperties.loadProperties(event.getWorld());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void worldUnload(WorldUnloadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(BitsWorlds.plugin, () -> new BWLoadedWorld(event.getWorld()).properties.save());
    }
}