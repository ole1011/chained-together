package de.ole101.chained.listeners;

import de.ole101.chained.common.annotations.RegisteredListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

@RegisteredListener
public class InteractionListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        // TODO
    }
}
