package de.ole101.chained.common.runnables;

import de.ole101.chained.common.models.Chain;
import de.ole101.chained.services.ChainService;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static de.ole101.chained.common.Colors.LIGHT_GRAY;
import static de.ole101.chained.common.Colors.YELLOW;
import static net.kyori.adventure.text.Component.keybind;
import static net.kyori.adventure.text.Component.text;

@RequiredArgsConstructor
public class ChainRunnable extends BukkitRunnable {

    private static final Component ACTION_BAR_COMPONENT = text("Tipp: Drücke", LIGHT_GRAY).appendSpace()
            .append(keybind("key.swapOffhand", YELLOW)).appendSpace()
            .append(text("um deinen Partner zu dir zu ziehen.", LIGHT_GRAY));
    private final ChainService chainService;
    private final Chain chain;

    @Override
    public void run() {
        Player player = this.chain.getPlayer();
        Player target = this.chain.getTarget();
        Slime slime = this.chain.getSlime();

        if (slime.isDead() || !player.isOnline() || !target.isOnline()) {
            this.chainService.getActiveChains().remove(this.chain);
            cancel();
            return;
        }

        player.sendActionBar(ACTION_BAR_COMPONENT);
        target.sendActionBar(ACTION_BAR_COMPONENT);

        double distance = player.getLocation().distance(target.getLocation());
        double chainLength = this.chain.getDifficulty().getChainLength();
        if (distance > chainLength) {
            long lastMovementPlayer = this.chainService.getLastMoved().getOrDefault(player.getUniqueId(), 0L);
            long lastMovementTarget = this.chainService.getLastMoved().getOrDefault(target.getUniqueId(), 0L);

            Player pullingPlayer = lastMovementPlayer > lastMovementTarget ? player : target;
            Player pulledPlayer = pullingPlayer == player ? target : player;

            boolean playerInAir = !player.isOnGround();
            boolean targetInAir = !target.isOnGround();

            boolean bothInAir = playerInAir && targetInAir;
            boolean onePlayerInAir = !bothInAir && (playerInAir || targetInAir);

            if (onePlayerInAir) {
                Player hangingPlayer = playerInAir ? player : target;

                pullingPlayer = hangingPlayer == player ? target : player;
                pulledPlayer = hangingPlayer;
            }

            if (bothInAir) {
                pullingPlayer = player.getY() > target.getY() ? target : player;
                pulledPlayer = pullingPlayer == player ? target : player;
            }

            Vector pullingPlayerLocationVector = pullingPlayer.getLocation().toVector();
            Vector pulledPlayerLocationVector = pulledPlayer.getLocation().toVector();
            double multiplier = Math.max(0.5, (distance - chainLength) * 0.5);

            Vector velocity = pullingPlayerLocationVector.subtract(pulledPlayerLocationVector).normalize().multiply(multiplier);
            pulledPlayer.setVelocity(velocity);
        }

        slime.teleport(target.getLocation().add(0, 0.5, 0));
    }
}
