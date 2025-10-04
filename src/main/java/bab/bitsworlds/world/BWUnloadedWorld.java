package bab.bitsworlds.world;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.UUID;

public class BWUnloadedWorld implements BWorld {
    private File file;
    private UUID uuid;

    @Override
    public String getName() {
        return file.getName();
    }

    public File getFile() {
        return file;
    }

    public BWUnloadedWorld(File file) {
        this.file = file;
    }

    @Override
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof BWorld that) {
            return Objects.equals(getName(), that.getName()) && Objects.equals(getUUID(), that.getUUID());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file.getName(), uuid);
    }
}
