package bab.bitsworlds.utils;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.world.BWBackup;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BackupUtils {
    public static List<BWBackup> getBackups() {
        List<BWBackup> list = new ArrayList<>();
        for (File file : new File(BitsWorlds.plugin.getDataFolder() + "/backups/").listFiles(File::isDirectory)) {
            BWBackup backup = BWBackup.getBackup(file, file.getName());
            if (backup == null)
                continue;
            list.add(backup);
        }
        return list;
    }

    public static List<BWBackup> getBackupsByWorld(String worldName) {
        return getBackups().stream().filter(bwBackup -> bwBackup.worldName.equals(worldName)).collect(Collectors.toList());
    }

    public static void recoverBackup(File backup) throws IOException {
        FileUtils.copyDirectory(backup, new File(Bukkit.getWorldContainer() + "/" + backup.getName()));
    }
}
