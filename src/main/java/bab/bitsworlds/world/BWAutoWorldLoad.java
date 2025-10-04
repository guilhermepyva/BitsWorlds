package bab.bitsworlds.world;

import bab.bitsworlds.BitsWorlds;
import bab.bitsworlds.db.BWSQL;
import bab.bitsworlds.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;

public class BWAutoWorldLoad {
    public static Map<UUID, String> autoLoadedWorldsInfo = new HashMap<>();
    public static List<BWorld> worlds = new ArrayList<>();

    public static void load() {
        autoLoadedWorldsInfo.forEach((uuid, string) -> {
            File file = new File(Bukkit.getWorldContainer(), string);
            if (!file.exists() || !file.isDirectory()) {
                BitsWorlds.logger.log(Level.WARNING, "Couldn't load " + string);
                return;
            }

            File uidFile = new File(file, "uid.dat");
            if (!uidFile.exists()) {
                BitsWorlds.logger.log(Level.WARNING, "Couldn't load " + string);
                return;
            }

            try {
                UUID worldUUID = FileUtils.getUIDFile(uidFile);

                if (!worldUUID.equals(uuid)) {
                    BitsWorlds.logger.log(Level.WARNING, "Couldn't load " + string + " because the UID isn't the same " +
                            "(current world uid: " + worldUUID + ")" +
                            " (audo-load-world uid: " + uuid + ")");
                    return;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            World world = Bukkit.createWorld(new WorldCreator(string));
            worlds.add(new BWLoadedWorld(world));

            BitsWorlds.logger.info(world.getName() + " loaded");
        });
    }

    public static boolean switchWorld(BWorld world, File file, UUID worldUUID) {
        try {
            Statement stm = BWSQL.dbCon.createStatement();

            if (stm.executeUpdate("DELETE FROM auto_load_worlds WHERE world = '" + worldUUID + "' AND worldName = '" + file.getName() + "'") > 0) {
                worlds.remove(world);
                stm.close();
                return false;
            } else {
                stm.executeUpdate("INSERT INTO auto_load_worlds  VALUES ('" + worldUUID + "', '" + file.getName() + "')");
                worlds.add(world);
            }

            stm.close();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
