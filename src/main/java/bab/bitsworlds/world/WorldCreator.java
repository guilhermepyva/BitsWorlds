package bab.bitsworlds.world;

import org.bukkit.World;
import org.bukkit.WorldType;

public class WorldCreator {
    public String name;
    public World.Environment environment;
    public Long seed;
    public boolean generateStructures;
    public WorldType worldType;
    public String generatorSettings;

    public boolean isComplete() {
        return name != null && environment != null && worldType != null;
    }
}
