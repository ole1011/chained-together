package de.ole101.chained.listeners;

import com.google.inject.Inject;
import de.ole101.chained.common.annotations.RegisteredListener;
import de.ole101.chained.common.models.Chain;
import de.ole101.chained.services.ChainService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.util.Vector;

import static de.ole101.chained.common.Colors.LIGHT_GRAY;
import static net.kyori.adventure.text.Component.text;
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

        Chain chain = this.chainService.getActiveChains().stream()
                .filter(c -> c.getPlayer().equals(player) || c.getTarget().equals(player))
                .findFirst()
                .orElse(null);
        if (chain != null) {

            if (player.isSneaking()) {
                Player chainPartner = chain.getPlayer().equals(player) ? chain.getTarget() : chain.getPlayer();

                if (!chainPartner.equals(target)) {
                    return;
                }

                this.chainService.disbandChain(chain);
                player.sendMessage(text("Du hast die Kette aufgelöst.", LIGHT_GRAY));
                chainPartner.sendMessage(text("Dein Partner hat die Kette aufgelöst.", LIGHT_GRAY));
            }

            return;
        }

        if (this.chainService.getRequestedChains().containsKey(player.getUniqueId())) {
            return;
        }

        boolean isRequestingTarget = this.chainService.getRequestedChains().entrySet().stream().anyMatch(entry -> entry.getKey().equals(target.getUniqueId()));
        if (this.chainService.getRequestedChains().containsValue(player.getUniqueId()) && isRequestingTarget) {
            this.chainService.acceptRequest(player, target);
            return;
        }

        this.chainService.requestChaining(player, target);
    }

    @EventHandler
    public void onSwapItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (this.chainService.getActiveChains().stream().noneMatch(chain -> chain.getPlayer().equals(player) || chain.getTarget().equals(player))) {
            return;
        }

        event.setCancelled(true);

        Player target = this.chainService.getActiveChains().stream()
                .filter(chain -> chain.getPlayer().equals(player) || chain.getTarget().equals(player))
                .map(chain -> chain.getPlayer().equals(player) ? chain.getTarget() : chain.getPlayer())
                .findFirst()
                .orElseThrow();

        Location playerLocation = player.getLocation();
        Location targetLocation = target.getLocation();
        Vector playerLocationVector = playerLocation.toVector();
        Vector targetLocationVector = targetLocation.toVector();

        Vector velocity = playerLocationVector.subtract(targetLocationVector).normalize().multiply(0.3);

        double playerY = player.getY();
        double targetY = target.getY();
        if (playerY != targetY) {
            velocity.setY(playerY > targetY ? 0.3 : -0.3);
        }

        if (playerLocation.distance(targetLocation) < 0.5) {
            velocity = new Vector();
        }

        target.setVelocity(velocity);
    }
}
