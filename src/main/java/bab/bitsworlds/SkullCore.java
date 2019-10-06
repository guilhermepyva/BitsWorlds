package bab.bitsworlds;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkullCore {
    public static Map<Skull, GameProfile> skulls;

    public static void loadSkulls() {
        skulls = new HashMap<>();

        for (Skull skull : Skull.values()) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), skull.name);

            profile.getProperties().put("textures", new Property(new String(Base64.getEncoder().encode(("\"textures\":{\"SKIN\":{\"url\":\"" + skull.path + "\"}}").getBytes())), "textures"));

            skulls.put(skull, profile);
        }
    }

    public enum Skull{
        TEST("download%20(1)", "bilbis");

        public String path;
        public String name;

        Skull(String path, String name) {
            this.path = path;
            this.name = name;
        }
    }
}
