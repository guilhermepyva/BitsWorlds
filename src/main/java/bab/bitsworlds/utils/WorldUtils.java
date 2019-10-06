package bab.bitsworlds.utils;

import bab.bitsworlds.extensions.BWLoadedWorld;
import bab.bitsworlds.extensions.BWUnloadedWorld;
import bab.bitsworlds.extensions.BWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldUtils {
    public static List<BWorld> getWorlds() {
        List<BWorld> worlds = new ArrayList<>();

        Bukkit.getWorlds().forEach(world -> worlds.add(new BWLoadedWorld(world)));
        getUnloadedWorlds().forEach(file -> worlds.add(new BWUnloadedWorld(file.getName())));

        return worlds;
    }

    public static int countWorlds() {
        int i = 0;
        for (World ignored : Bukkit.getWorlds()) {
            i++;
        }

        for (Object ignored : getUnloadedWorlds().toArray()) {
            i++;
        }

        return i;
    }

    public static Stream<File> getUnloadedWorlds() {
        List<String> loadedWorldNames = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());

        FileFilter fileFilter = pathname -> pathname.isDirectory();

        return Arrays.stream(Bukkit.getWorldContainer().listFiles(fileFilter))
                .filter(file -> new File(file + "/level.dat").exists())
                .filter(file -> !loadedWorldNames.contains(file.getName()));
    }
}
