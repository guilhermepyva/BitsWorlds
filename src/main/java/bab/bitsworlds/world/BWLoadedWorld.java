package bab.bitsworlds.world;

import org.bukkit.World;

import java.util.Objects;
import java.util.UUID;

public class BWLoadedWorld implements BWorld {
    public World world;

    @Override
    public String getName() {
        return world.getName();
    }

    public World getWorld() {
        return world;
    }

    @Override
    public UUID getUUID() {
        return world.getUID();
    }

    public BWLoadedWorld(World world) {
        this.world = world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof BWorld that) {
            return Objects.equals(getName(), that.getName()) && Objects.equals(getUUID(), that.getUUID());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(world.getName(), world.getUID());
    }
}
