package de.ole101.chained.common.runnables;

import de.ole101.chained.common.models.Chain;
import de.ole101.chained.services.ChainService;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class ChainRunnable extends BukkitRunnable {

    private final ChainService chainService;
    private final Chain chain;

    @Override
    public void run() {
        Player player = this.chain.getPlayer();
        Player target = this.chain.getTarget();
        Slime slime = this.chain.getSlime();

        //TODO: Proper check when to cancel (disconnected, etc.)
        if (slime.isDead()) {
            this.chainService.getActiveChains().remove(this.chain);
            cancel();
            return;
        }

        double distance = player.getLocation().distance(target.getLocation());
        if (distance > 5) {
            long lastMovementPlayer = this.chainService.getLastMoved().getOrDefault(player.getUniqueId(), 0L);
            long lastMovementTarget = this.chainService.getLastMoved().getOrDefault(target.getUniqueId(), 0L);

            Player pullingPlayer = lastMovementPlayer > lastMovementTarget ? player : target;
            Player pulledPlayer = pullingPlayer == player ? target : player;

            pulledPlayer.setVelocity(pullingPlayer.getLocation().toVector().subtract(pulledPlayer.getLocation().toVector()).normalize().multiply(0.5));
        }

        slime.teleport(target.getLocation().add(0, 0.5, 0));
    }
}
