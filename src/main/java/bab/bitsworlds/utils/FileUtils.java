package bab.bitsworlds.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

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
}