package bab.bitsworlds.world;

import bab.bitsworlds.utils.WorldUtils;

import java.io.File;
import java.sql.Timestamp;

public class BWBackup {
    public File file;
    public String worldName;
    public Timestamp timestamp;

    public BWBackup(File file, String worldName, Timestamp timestamp) {
        this.worldName = worldName;
        this.timestamp = timestamp;
        this.file = file;
    }

    @Override
    public String toString() {
        return worldName + "." + timestamp.getTime();
    }

    public static BWBackup getBackup(File file, String string) {
        String[] properties = string.split("\\.");
        if (properties.length < 2) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= properties.length - 2; i++) {
            sb.append(properties[i]);
        }

        String worldName = WorldUtils.getValidWorldName(sb.toString());
        if (worldName.isEmpty())
            return null;

        long timestamp;
        try {
            timestamp = Long.parseLong(properties[properties.length -1]);
        } catch (NumberFormatException e) {
            return null;
        }

        return new BWBackup(file, worldName, new Timestamp(timestamp));
    }
}
