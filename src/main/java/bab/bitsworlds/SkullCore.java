package bab.bitsworlds;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkullCore {
    private static Map<Skull, GameProfile> skulls;

    public static void loadSkulls() {
        skulls = new HashMap<>();

        for (Skull skull : Skull.values()) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);

            profile.getProperties().put("textures", new Property(new String(Base64.getEncoder().encode(("\"textures\":{\"SKIN\":{\"url\":\"" + skull.getPath() + "\"}}").getBytes())), "textures"));

            skulls.put(skull, profile);
        }
    }

    public static void applyToSkull(SkullMeta meta, Skull skull) {
        try {
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, skulls.get(skull));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Skull{
        BWICON("261be940fde0b6cb92e80a5d2a405f34f9ebd76d158b399506f182711ab27777");

        private String path;

        Skull(String path) {
            this.path = path;
        }

        public String getPath() {
            return "http://textures.minecraft.net/texture/" + path;
        }
    }
}