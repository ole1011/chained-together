package de.ole101.chained.listeners;

import com.google.inject.Inject;
import de.ole101.chained.common.annotations.RegisteredListener;
import de.ole101.chained.services.ChainService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import static org.bukkit.inventory.EquipmentSlot.OFF_HAND;

@RegisteredListener
public class InteractionListener implements Listener {

    @Inject
    private ChainService chainService;

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() == OFF_HAND) {
            return;
        }
        if (!(event.getRightClicked() instanceof Player target)) {
            return;
        }

        if (this.chainService.getActiveChains().stream().anyMatch(chain -> chain.getPlayer().equals(player) || chain.getTarget().equals(player))) {
            return;
        }

        this.chainService.requestChaining(player, target);
//        this.chainService.chainTogether(player, target);
    }
}
