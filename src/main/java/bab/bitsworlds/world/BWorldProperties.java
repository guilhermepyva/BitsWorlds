package bab.bitsworlds.world;

import org.bukkit.World;
import org.json.JSONObject;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BWorldProperties {
    File propertiesFile;
    String aliasName;
    boolean autoload;
    boolean saveOnExit;

    public BWorldProperties(File propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public void save() {
        JSONObject jsonObject = new JSONObject();
        if (aliasName != null)
            jsonObject.put("alias-name", aliasName);
        jsonObject.put("auto-load", autoload);
        jsonObject.put("save-on-exit", saveOnExit);

        try {
            if (propertiesFile.exists())
                propertiesFile.createNewFile();

            FileWriter writer = new FileWriter(propertiesFile);
            writer.write(jsonObject.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject read() {
        if (!propertiesFile.exists())
            return null;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(propertiesFile));
            JSONObject jsonObject = new JSONObject(reader.readLine());
            reader.close();
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<UUID, BWorldProperties> propertiesCache = new ConcurrentHashMap<>();

    public static void loadProperties(World world) {
        getProperties(new BWLoadedWorld(world));
    }

    public static BWorldProperties getProperties(BWorld world) {
        if (propertiesCache.containsKey(world.getUUID()))
            return propertiesCache.get(world.getUUID());

        File worldFolder = world instanceof BWLoadedWorld ? ((BWLoadedWorld) world).getWorld().getWorldFolder() : ((BWUnloadedWorld) world).getFile();
        File propertiesFile = new File(worldFolder, "bwproperties.json");

        BWorldProperties worldProperties = new BWorldProperties(propertiesFile);

        if (!propertiesFile.exists()) {
            worldProperties.aliasName = (String) getDefaultValue("alias-name");
            worldProperties.autoload = (Boolean) getDefaultValue("auto-load");
            worldProperties.saveOnExit = (Boolean) getDefaultValue("save-on-exit");
            worldProperties.save();
        } else {
            JSONObject jsonObject = worldProperties.read();

            if (jsonObject.has("alias-name"))
                worldProperties.aliasName = jsonObject.getString("alias-name");
            worldProperties.autoload = jsonObject.optBoolean("auto-load");
            worldProperties.saveOnExit = jsonObject.optBoolean("save-on-exit");
        }

        propertiesCache.put(world.getUUID(), worldProperties);
        return worldProperties;
    }

    private static Object getDefaultValue(String string) {
        switch (string) {
            case "alias-name":
                return null;
            case "auto-load":
                return false;
            case "save-on-exit":
                return true;
        }
        return null;
    }
}