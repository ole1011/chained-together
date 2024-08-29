package de.ole101.chained.listeners;

import com.google.inject.Inject;
import de.ole101.chained.common.annotations.RegisteredListener;
import de.ole101.chained.services.ChainService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import static de.ole101.chained.common.Colors.LIGHT_GRAY;
import static de.ole101.chained.common.Colors.YELLOW;
import static java.util.Optional.ofNullable;
import static net.kyori.adventure.text.Component.text;

@RegisteredListener
public class SessionListener implements Listener {

    @Inject
    private ChainService chainService;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Scoreboard scoreboard = player.getScoreboard();
        Team team = ofNullable(scoreboard.getTeam("NoCollision")).orElseGet(() -> scoreboard.registerNewTeam("NoCollision"));

        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addPlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.chainService.getActiveChains().stream()
                .filter(chain -> chain.getPlayer().equals(player) || chain.getTarget().equals(player))
                .findFirst()
                .ifPresent(chain -> {
                    Player target = chain.getPlayer() == player ? chain.getTarget() : player;
                    this.chainService.getActiveChains().remove(chain);
                    chain.disband();

                    target.sendMessage(text("Da", LIGHT_GRAY).appendSpace()
                            .append(text(player.getName(), YELLOW)).appendSpace()
                            .append(text("den Server verlassen hat, wurde die Kette aufgelöst.", LIGHT_GRAY)));
                });
    }
}