package bab.bitsworlds;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.mojang.authlib.GameProfile;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullMetaDriver {
    public static void applyTexture(SkullMeta meta, GameProfile gameProfile) {
        meta.setPlayerProfile(new CraftPlayerProfile(gameProfile));
    }
}
