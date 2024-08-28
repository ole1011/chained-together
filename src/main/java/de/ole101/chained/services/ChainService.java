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
        chain.attachLeash();

        BukkitTask task = new ChainRunnable(this, chain).runTaskTimer(this.chainedTogether, 0, 1);
        chain.setTask(task);

        this.activeChains.add(chain);
    }
}
