package bab.bitsworlds;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkullCore {
    private static Map<Skull, GameProfile> skulls;

    public static void loadSkulls() {
        skulls = new HashMap<>();

        for (Skull skull : Skull.values()) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "");

            profile.getProperties().put("textures", new Property("textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skull.getPath() + "\"}}}").getBytes()))));

            skulls.put(skull, profile);
        }
    }

    public static void applyToSkull(SkullMeta meta, Skull skull) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
        profile.getProperties().add(new ProfileProperty("textures", "textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skull.getPath() + "\"}}}").getBytes()))));

        meta.setPlayerProfile(profile);

        applyTexture(meta, skulls.get(skull));
    }

    private static void applyTexture(SkullMeta meta, GameProfile texture) {
        meta.setPlayerProfile(new CraftPlayerProfile(texture));
    }

    public enum Skull {
        BWICON("261be940fde0b6cb92e80a5d2a405f34f9ebd76d158b399506f182711ab27777"),
        LISTWORLDICON("160729c4d21bcf0535e0988593f9e19458de35ff7ddb8dcd223fb448d627c0be"),
        CREATEWORLDICON("f5e629bd7326def31557eeab5fbb11dc8e68109cfce961b817e9f16b53dd49a4"),
        AUTOLOADWORLDICON("b4518552f4468beedea612b1012ba185fe90c878c58d239b8eefbd49de6f60b4");

        private String path;

        Skull(String path) {
            this.path = path;
        }

        public String getPath() {
            return "https://textures.minecraft.net/texture/" + path;
        }
    }
}
