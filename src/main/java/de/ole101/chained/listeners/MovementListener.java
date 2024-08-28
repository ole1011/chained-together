package de.ole101.chained.listeners;

import com.google.inject.Inject;
import de.ole101.chained.common.annotations.RegisteredListener;
import de.ole101.chained.services.ChainService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

@RegisteredListener
public class MovementListener implements Listener {

    @Inject
    private ChainService chainService;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Vector velocity = player.getVelocity();

        if (velocity.length() > 0.1 && !player.isClimbing()) {
            return;
        }

        if (!player.isOnGround()) {
            return;
        }

        if (!event.hasChangedPosition()) {
            return;
        }

        chainService.getLastMoved().put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }
}