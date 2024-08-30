package de.ole101.chained.services;

import com.google.inject.Inject;
import de.ole101.chained.ChainedTogether;
import de.ole101.chained.common.enums.Difficulty;
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
import static net.kyori.adventure.text.event.ClickEvent.callback;

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
        Component playerComponent = text("Du hast die Coop-Anfrage von", LIGHT_GRAY).appendSpace()
                .append(text(target.getName(), YELLOW)).appendSpace()
                .append(text("angenommen.", GREEN));

        Component targetComponent = text(player.getName(), YELLOW).appendSpace()
                .append(text("hat die Coop-Anfrage", LIGHT_GRAY)).appendSpace()
                .append(text("angenommen.", GREEN));

        Component difficultyComponent = text("Wähle nun eine Schwierigkeit aus:", LIGHT_GRAY).appendSpace();

        for (Difficulty difficulty : Difficulty.values()) {
            difficultyComponent = difficultyComponent.append(text(difficulty.getDisplayName(), difficulty.getColor())
                    .hoverEvent(text("Klicke zum auswählen", LIGHT_GRAY))
                    .clickEvent(callback(audience -> {
                        if (this.activeChains.stream().anyMatch(chain -> chain.getPlayer().equals(player) || chain.getTarget().equals(player))) {
                            return;
                        }

                        this.requestedChains.remove(target.getUniqueId());

                        Component selectedDifficultyComponent = text("Du hast die Schwierigkeit", LIGHT_GRAY).appendSpace()
                                .append(text(difficulty.getDisplayName(), difficulty.getColor())).appendSpace()
                                .append(text("ausgewählt.", LIGHT_GRAY));

                        Component selectedDifficultyComponentTarget = text(player.getName(), YELLOW).appendSpace()
                                .append(text("hat die Schwierigkeit", LIGHT_GRAY)).appendSpace()
                                .append(text(difficulty.getDisplayName(), difficulty.getColor())).appendSpace()
                                .append(text("ausgewählt.", LIGHT_GRAY));

                        target.sendMessage(selectedDifficultyComponent);
                        player.sendMessage(selectedDifficultyComponentTarget);

                        chainTogether(target, player, difficulty);
                    }))).appendSpace();
        }

        player.sendMessage(playerComponent);
        target.sendMessage(targetComponent);
        target.sendMessage(difficultyComponent);
    }

    public void chainTogether(Player player, Player target, Difficulty difficulty) {
        Chain chain = Chain.builder()
                .player(player)
                .target(target)
                .difficulty(difficulty)
                .build();
        chain.attachLeash();

        BukkitTask task = new ChainRunnable(this, chain).runTaskTimer(this.chainedTogether, 0, 1);
        chain.setTask(task);

        this.activeChains.add(chain);
    }
}
