package de.ole101.chained.services;

import com.google.inject.Inject;
import de.ole101.chained.ChainedTogether;
import de.ole101.chained.common.models.Chain;
import de.ole101.chained.common.runnables.ChainRunnable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static de.ole101.chained.common.Colors.BLUE;
import static de.ole101.chained.common.Colors.GREEN;
import static de.ole101.chained.common.Colors.LIGHT_GRAY;
import static de.ole101.chained.common.Colors.YELLOW;
import static net.kyori.adventure.text.Component.text;

@Getter
@RequiredArgsConstructor
public class ChainService {

    @Inject
    public ChainedTogether chainedTogether;

    private final Set<Chain> activeChains = new HashSet<>();
    private final Map<UUID, UUID> requestedChains = new HashMap<>();
    private final HashMap<UUID, Long> lastMoved = new HashMap<>();

    public void requestChaining(Player player, Player target) {
        Component playerComponent = text("Du hast", LIGHT_GRAY).appendSpace()
                .append(text(target.getName(), YELLOW)).appendSpace()
                .append(text("eine Coop-Anfrage gesendet.", LIGHT_GRAY));

        Component targetComponent = text("Du hast eine Coop-Anfrage von", LIGHT_GRAY).appendSpace()
                .append(text(player.getName(), YELLOW)).appendSpace()
                .append(text("erhalten.", LIGHT_GRAY));

        Component titleComponent = text("Interagiere mit", LIGHT_GRAY).appendSpace()
                .append(text(player.getName(), YELLOW))
                .append(text(", um die Anfrage zu akzeptieren.", LIGHT_GRAY));

        player.sendActionBar(playerComponent);
        target.sendMessage(targetComponent);
        target.sendMessage(titleComponent);
        target.showTitle(Title.title(text("Coop-Einladung", BLUE), titleComponent, Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofMillis(500))));

        this.requestedChains.put(player.getUniqueId(), target.getUniqueId());
    }

    public void acceptRequest(Player player, Player target) {
        this.requestedChains.remove(target.getUniqueId());

        Component playerComponent = text("Du hast die Coop-Anfrage von", LIGHT_GRAY).appendSpace()
                .append(text(target.getName(), YELLOW)).appendSpace()
                .append(text("angenommen.", GREEN));

        Component targetComponent = text(player.getName(), YELLOW).appendSpace()
                .append(text("hat die Coop-Anfrage", LIGHT_GRAY)).appendSpace()
                .append(text("angenommen.", GREEN));

        player.sendMessage(playerComponent);
        target.sendMessage(targetComponent);

        chainTogether(target, player);
    }

    public void chainTogether(Player player, Player target) {
        Chain chain = Chain.builder()
                .player(player)
                .target(target)
                .build();
        chain.attachLeash();

        BukkitTask task = new ChainRunnable(this, chain).runTaskTimer(this.chainedTogether, 0, 1);
        chain.setTask(task);

        this.activeChains.add(chain);
    }
}
