package de.ole101.chained.listeners;

import com.google.inject.Inject;
import de.ole101.chained.common.annotations.RegisteredListener;
import de.ole101.chained.services.ChainService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import static org.bukkit.entity.EntityType.SLIME;

@RegisteredListener
public class DeathListener implements Listener {

    @Inject
    private ChainService chainService;

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!event.getEntityType().equals(SLIME)) {
            return;
        }

        boolean isChain = this.chainService.getActiveChains().stream()
                .anyMatch(chain -> chain.getSlime().equals(event.getEntity()));

        event.setCancelled(isChain);
    }

}
