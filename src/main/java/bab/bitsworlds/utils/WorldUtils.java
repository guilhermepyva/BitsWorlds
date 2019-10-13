package bab.bitsworlds.utils;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.world.BWBackup;
import bab.bitsworlds.world.BWLoadedWorld;
import bab.bitsworlds.world.BWUnloadedWorld;
import bab.bitsworlds.world.BWorld;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldUtils {
    public static List<BWorld> getWorlds() {
        List<BWorld> worlds = new ArrayList<>();

        Bukkit.getWorlds().forEach(world -> worlds.add(new BWLoadedWorld(world)));
        getStreamUnloadedWorlds().forEach(file -> worlds.add(new BWUnloadedWorld(file)));

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

        return Arrays.stream(Bukkit.getWorldContainer().listFiles(File::isDirectory))
                .filter(file -> new File(file + "/level.dat").exists())
                .filter(file -> !loadedWorldNames.contains(file.getName()));
    }

    public static List<BWorld> getUnloadedWorlds() {
        List<BWorld> list = new ArrayList<>();
        getStreamUnloadedWorlds().forEach(file -> list.add(new BWUnloadedWorld(file)));
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

    public static BWUnloadedWorld getUnloadedWorld(String name) {
        return new BWUnloadedWorld(new File(Bukkit.getWorldContainer() + "/" + name));
    }

    public static void copyWorld(String world, File to) throws IOException {
        FileUtils.copyDirectory(new File(Bukkit.getWorldContainer() + "/" + world), to);
        new File(to + "/uid.dat").delete();
    }

    public static String getValidWorldName(String string) {
        return string.replace("/", "").replace("\\", "").replace(".", "");
    }
}
