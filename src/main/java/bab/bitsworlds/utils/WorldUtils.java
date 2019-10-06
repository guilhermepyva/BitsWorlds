package bab.bitsworlds.utils;

import bab.bitsworlds.extensions.BWLoadedWorld;
import bab.bitsworlds.extensions.BWUnloadedWorld;
import bab.bitsworlds.extensions.BWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldUtils {
    public static List<BWorld> getWorlds() {
        List<BWorld> worlds = new ArrayList<>();

        Bukkit.getWorlds().forEach(world -> worlds.add(new BWLoadedWorld(world)));
        getStreamUnloadedWorlds().forEach(file -> worlds.add(new BWUnloadedWorld(file.getName())));

        return worlds;
    }

    public static int countWorlds() {
        int i = 0;
        for (World ignored : Bukkit.getWorlds()) {
            i++;
        }

        for (Object ignored : getStreamUnloadedWorlds().toArray()) {
            i++;
        }

        return i;
    }

    public static Stream<File> getStreamUnloadedWorlds() {
        List<String> loadedWorldNames = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());

        FileFilter fileFilter = pathname -> pathname.isDirectory();

        return Arrays.stream(Bukkit.getWorldContainer().listFiles(fileFilter))
                .filter(file -> new File(file + "/level.dat").exists())
                .filter(file -> !loadedWorldNames.contains(file.getName()));
    }

    public static List<BWorld> getUnloadedWorlds() {
        List<BWorld> list = new ArrayList<>();
        getStreamUnloadedWorlds().forEach(file -> list.add(new BWUnloadedWorld(file.getName())));
        return list;
    }

    public static List<BWorld> getLoadedWorlds() {
        List<BWorld> list = new ArrayList<>();
        Bukkit.getWorlds().forEach(world -> list.add(new BWLoadedWorld(world)));
        return list;
    }

    public static int countUnloadedWorlds() {
        int i = 0;
        for (Object ignored : getStreamUnloadedWorlds().toArray()) {
            i++;
        }
        return i;
    }
}
