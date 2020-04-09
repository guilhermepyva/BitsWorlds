package bab.bitsworlds.world;

import org.bukkit.World;

import java.util.UUID;

public class BWLoadedWorld extends BWorld {
    public World world;

    public World getWorld() {
        return world;
    }

    public BWLoadedWorld(World world) {
        this.world = world;
        this.properties = BWorldProperties.getProperties(this);
    }

    @Override
    public String getName() {
        return world.getName();
    }

    @Override
    public UUID getUUID() {
        return world.getUID();
    }
}
