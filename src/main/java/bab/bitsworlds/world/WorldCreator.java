package bab.bitsworlds.world;

import bab.bitsworlds.cmd.CreateWorldCmd;
import org.bukkit.World;

public class WorldCreator {
    public String name;
    public World.Environment environment;
    public Long seed;
    public boolean generateStructures;
    public CreateWorldCmd.WorldType worldType;
    public String generatorSettings;

    public boolean isComplete() {
        return name != null && environment != null && worldType != null;
    }
}
