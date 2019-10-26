package bab.bitsworlds.world;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

public class BWUnloadedWorld implements BWorld {
    private File file;
    public UUID uuid;

    @Override
    public String getName() {
        return file.getName();
    }

    public BWUnloadedWorld(File file) {
        this.file = file;
    }

    public UUID getUUID() {
        if (uuid != null)
            return uuid;

        try {
            File uiddat = new File(file + "/uid.dat");
            if (!uiddat.exists())
                return null;

            DataInputStream out = new DataInputStream(new FileInputStream(new File(file + "/uid.dat")));

            uuid = new UUID(out.readLong(), out.readLong());
            return uuid;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
