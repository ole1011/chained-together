package de.ole101.chained.services;

import com.google.inject.Inject;
import de.ole101.chained.ChainedTogether;
import de.ole101.chained.common.models.Chain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static de.ole101.chained.common.Colors.GRAY;
import static de.ole101.chained.common.Colors.YELLOW;
import static net.kyori.adventure.text.Component.keybind;
import static net.kyori.adventure.text.Component.text;

@Getter
@RequiredArgsConstructor
public class ChainService {

    @Inject
    public ChainedTogether chainedTogether;

    private final Set<Chain> activeChains = new HashSet<>();
    private final HashMap<UUID, Long> lastMoved = new HashMap<>();

    public void requestChaining(Player player, Player target) {
        // TODO: find a better way
        Component playerComponent = text("Du hast", GRAY).appendSpace()
                .append(text(target.getName(), YELLOW)).appendSpace()
                .append(text("eine Anfrage für den Coop-Modus gesendet.", GRAY));

        Component targetComponent = text("Du hast eine Anfrage für den Coop-Modus von", GRAY).appendSpace()
                .append(text(player.getName(), YELLOW)).appendSpace()
                .append(text("erhalten.", GRAY));

        Component titleComponent = text("Halte", GRAY).appendSpace()
                .append(keybind("key.sneak", YELLOW)).appendSpace()
                .append(text("gedrückt, um zu akzeptieren.", GRAY));

        player.sendActionBar(playerComponent);
        target.sendActionBar(targetComponent);
        target.showTitle(Title.title(text(""), titleComponent));
    }

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
                    long lastMovementPlayer = ChainService.this.lastMoved.getOrDefault(player.getUniqueId(), 0L);
                    long lastMovementTarget = ChainService.this.lastMoved.getOrDefault(target.getUniqueId(), 0L);
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
