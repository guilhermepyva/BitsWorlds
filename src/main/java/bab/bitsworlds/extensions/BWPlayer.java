package bab.bitsworlds.extensions;

import bab.bitsworlds.gui.BWGUI;
import bab.bitsworlds.gui.GUIHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

/**
 * This is an extension class for players to work effectively with the plugin
 */
public class BWPlayer extends BWCommandSender {
    private Player player;

    public Player getBukkitPlayer() {
        return player;
    }

    public BWPlayer(Player player) {
        super(player);
        this.player = player;
    }

    public BWPlayer(UUID uuid) {
        this(Bukkit.getPlayer(uuid));
    }

    public BWPlayer(String name) {
        this(Bukkit.getPlayerExact(name));
    }

    public void openGUI(BWGUI gui) {
        GUIHandler.openGUIs.put(this, gui);
        this.getBukkitPlayer().openInventory(gui);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BWPlayer bwPlayer = (BWPlayer) o;
        return Objects.equals(player, bwPlayer.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
