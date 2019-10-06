package bab.bitsworlds.extensions;

import org.bukkit.World;

public class BWLoadedWorld implements BWorld {
    public World world;

    @Override
    public String getName() {
        return world.getName();
    }

    public World getWorld() {
        return world;
    }

    public BWLoadedWorld(World world) {
        this.world = world;
    }
}
