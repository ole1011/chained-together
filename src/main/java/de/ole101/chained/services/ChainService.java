package de.ole101.chained.services;

import com.google.inject.Inject;
import de.ole101.chained.ChainedTogether;
import de.ole101.chained.common.models.Chain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ChainService {

    @Inject
    public ChainedTogether chainedTogether;

    private final List<Chain> activeChains = new ArrayList<>();
    private final HashMap<UUID, Long> lastMoved = new HashMap<>();

    public void chainTogether(Player player, Player target) {
        Chain chain = Chain.builder()
                .player(player)
                .target(target)
                .build();
        Slime slime = chain.attachLeash();

        // TODO: clean up
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                //TODO: Proper check when to cancel (disconnected, etc.)
                if (slime.isDead()) {
                    ChainService.this.activeChains.remove(chain);
                    cancel();
                    return;
                }

                double distance = player.getLocation().distance(target.getLocation());
                if (distance > 5) {
                    long lastMovementPlayer = lastMoved.getOrDefault(player.getUniqueId(), 0L);
                    long lastMovementTarget = lastMoved.getOrDefault(target.getUniqueId(), 0L);
                    Player pullingPlayer = lastMovementPlayer > lastMovementTarget ? player : target;
                    Player pulledPlayer = pullingPlayer == player ? target : player;

                    pulledPlayer.setVelocity(pullingPlayer.getLocation().toVector().subtract(pulledPlayer.getLocation().toVector()).normalize().multiply(0.5));
                }

                slime.teleport(target.getLocation().add(0, 0.5, 0));
            }
        }.runTaskTimer(this.chainedTogether, 0, 1);

        chain.setTask(task);
        this.activeChains.add(chain);
    }
}
