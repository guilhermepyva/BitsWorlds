package bab.bitsworlds;

import bab.bitsworlds.extensions.BWPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChatInput implements Listener {
    public static Map<BWPlayer, String> inputPlayers;

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (inputPlayers.containsKey(new BWPlayer(event.getPlayer()))) {
            inputPlayers.put(new BWPlayer(event.getPlayer()), event.getMessage());

            event.setCancelled(true);
        }
    }

    public static String askPlayer(BWPlayer player) {
        inputPlayers.put(player, null);

        if (Thread.currentThread().getName().equals("Server thread")) {
            throw new RuntimeException("ChatInput.askPlayer can't run in Server thread");
        }

        try {
            while (true) {
                if (inputPlayers.get(player) != null) {
                    String response = inputPlayers.get(player);
                    inputPlayers.remove(player);

                    return response;
                }

                TimeUnit.MILLISECONDS.sleep(50);
            }
        } catch (Exception e) {
            BitsWorlds.logger.severe("A exception was found in ChatInput");

            e.printStackTrace();
            return null;
        }
    }
}
