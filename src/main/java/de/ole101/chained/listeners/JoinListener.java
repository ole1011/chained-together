package de.ole101.chained.listeners;

import de.ole101.chained.common.annotations.RegisteredListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import static java.util.Optional.ofNullable;

@RegisteredListener
public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Scoreboard scoreboard = player.getScoreboard();
        Team team = ofNullable(scoreboard.getTeam("NoCollision")).orElse(scoreboard.registerNewTeam("NoCollision"));

        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addPlayer(player);
    }
}