package de.ole101.chained.services;

import de.ole101.chained.ChainedTogether;
import de.ole101.chained.common.models.Chain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ChainService {

    public final ChainedTogether chainedTogether;
    private final List<Chain> activeChains = new ArrayList<>();

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

                slime.teleport(target.getLocation().add(0, 0.5, 0));
            }
        }.runTaskTimer(this.chainedTogether, 0, 1);

        chain.setTask(task);
        this.activeChains.add(chain);
    }
}
