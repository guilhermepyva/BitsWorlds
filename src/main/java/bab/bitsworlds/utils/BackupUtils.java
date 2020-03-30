package bab.bitsworlds.utils;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.world.BWBackup;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupUtils {
    public static List<BWBackup> getBackups() {
        List<BWBackup> list = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(BitsWorlds.plugin.getDataFolder() + "/backups/").listFiles(
                (dir, name) -> name.endsWith(".zip")
        ))) {
            BWBackup backup = BWBackup.getBackup(file, file.getName());
            if (backup == null)
                continue;
            list.add(backup);
        }
        return list;
    }

    public static List<BWBackup> getBackupsByWorld(String worldName, boolean ignoreCase) {
        return getBackups().stream().filter(bwBackup -> ignoreCase ? bwBackup.worldName.equalsIgnoreCase(worldName) : bwBackup.worldName.equals(worldName)).collect(Collectors.toList());
    }

    public static void recoverBackup(File backup) throws IOException {
        File worldFile = new File(Bukkit.getWorldContainer() + "/" + backup.getName().replace(".zip", ""));
        worldFile.mkdirs();

        byte[] buffer = new byte[1024];
        ZipInputStream in = new ZipInputStream(new FileInputStream(backup));
        ZipEntry zipEntry = in.getNextEntry();

        while (zipEntry != null) {
            File newFile = new File(Bukkit.getWorldContainer() + "/" + zipEntry.getName());
            if (zipEntry.getName().endsWith("/")) {
                newFile.mkdir();
                zipEntry = in.getNextEntry();
                continue;
            }
            else
                newFile.createNewFile();


            FileOutputStream fileOut = new FileOutputStream(newFile);
            int len;
            while ((len = in.read(buffer)) > 0) {
                fileOut.write(buffer, 0, len);
            }
            zipEntry = in.getNextEntry();
            fileOut.close();
        }
        in.closeEntry();
        in.close();
    }

    public static void makeBackup(String worldName) throws IOException {
        File worldFile = new File(Bukkit.getWorldContainer() + "/" + worldName);

        long millis = System.currentTimeMillis();

        FileOutputStream fileOut = new FileOutputStream(new File(BitsWorlds.plugin.getDataFolder() + "/backups/" + worldName + "." + millis + ".zip"));
        ZipOutputStream out = new ZipOutputStream(fileOut);

        zipFiles(worldFile, worldFile.getName() + "." + millis, out);

        out.close();
    }

    private static void zipFiles(File fileToZip, String fileName, ZipOutputStream out) throws IOException {
        for (File childFile : fileToZip.listFiles()) {
            if (childFile.isDirectory()) {
                out.putNextEntry(new ZipEntry(fileName + "/" + childFile.getName() + "/"));
                out.closeEntry();

                zipFiles(childFile, fileName + "/" + childFile.getName() , out);
                continue;
            }

            if (childFile.getName().equals("uid.dat"))
                continue;

            FileInputStream fileIn = new FileInputStream(childFile);
            out.putNextEntry(new ZipEntry(fileName + "/" + childFile.getName()));

            byte[] bytes = new byte[1024];
            int length;
            while((length = fileIn.read(bytes)) >= 0) {
                out.write(bytes, 0, length);
            }

            out.closeEntry();
            fileIn.close();
        }
    }
}
