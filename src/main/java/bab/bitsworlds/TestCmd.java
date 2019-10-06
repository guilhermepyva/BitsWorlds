package bab.bitsworlds;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public class TestCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        EntityPlayer entityPlayer = ((CraftPlayer) sender).getHandle();

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", "").getBytes());
            gameProfile.getProperties().put("textures", new Property("textures", new String(encodedData)));
            profileField.set(meta, gameProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        item.setItemMeta(meta);

        ((Player) sender).getInventory().addItem(item);

        ((CraftPlayer) sender).getHandle().getProfile().getProperties().get("textures").forEach(
                property -> {
                    System.out.println(property.getName());
                    System.out.println(property.getValue());
                }
        );

        return true;
    }
}
