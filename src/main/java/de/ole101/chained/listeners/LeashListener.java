package de.ole101.chained.listeners;

import com.google.inject.Inject;
import de.ole101.chained.common.annotations.RegisteredListener;
import de.ole101.chained.services.ChainService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;

@RegisteredListener
public class LeashListener implements Listener {

    @Inject
    private ChainService chainService;

    @EventHandler
    public void onLeash(EntityUnleashEvent event) {
        boolean isChain = this.chainService.getActiveChains().stream()
                .anyMatch(chain -> chain.getSlime().equals(event.getEntity()));

        event.setCancelled(isChain);
    }
}
