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
    public void onKick(PlayerKickEvent e) {
        if (e.getCause() != FLYING_PLAYER) {
            return;
        }

        Player p = e.getPlayer();
        boolean isChained = chainService.getActiveChains().stream()
                .anyMatch(chain -> chain.getPlayer().equals(p) || chain.getTarget().equals(p));

        e.setCancelled(isChained);
    }

}
