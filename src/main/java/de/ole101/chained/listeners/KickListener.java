package de.ole101.chained.listeners;

import com.google.inject.Inject;
import de.ole101.chained.common.annotations.RegisteredListener;
import de.ole101.chained.services.ChainService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import static org.bukkit.event.player.PlayerKickEvent.Cause.FLYING_PLAYER;

@RegisteredListener
public class KickListener implements Listener {

    @Inject
    private ChainService chainService;

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (event.getCause() != FLYING_PLAYER) {
            return;
        }

        Player player = event.getPlayer();
        boolean isChained = this.chainService.getActiveChains().stream()
                .anyMatch(chain -> chain.getPlayer().equals(player) || chain.getTarget().equals(player));

        event.setCancelled(isChained);
    }
}
