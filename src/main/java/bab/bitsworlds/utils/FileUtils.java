package bab.bitsworlds.utils;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

public class FileUtils {
    public static void copyContent(File directoryContentToCopy, File directoryToPaste) {
        directoryToPaste.mkdirs();
        try {
            for (File file : Objects.requireNonNull(directoryContentToCopy.listFiles())) {
                Files.copy(file.toPath(), new File(directoryToPaste.getAbsolutePath() + "/" + file.getName()).toPath());
                if (file.isDirectory())
                    copyContent(file, new File(directoryToPaste.getPath() + "/" + file.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(File file) throws SecurityException {
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();

            if (childFiles != null && childFiles.length > 0)
                for (File childFile : childFiles)
                    delete(childFile);
            file.delete();
            return;
        }

        file.delete();
    }

    public static UUID getUIDFile(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        UUID uuid = UUID.nameUUIDFromBytes(in.readAllBytes());
        in.close();
        return uuid;
    }
}