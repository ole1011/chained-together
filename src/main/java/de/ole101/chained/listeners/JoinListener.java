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
        Player p = event.getPlayer();
        Scoreboard sb = p.getScoreboard();

        ofNullable(sb.getTeam("NoCollision")).ifPresentOrElse(team -> team.addPlayer(p), () -> {
            Team team = sb.registerNewTeam("NoCollision");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.addPlayer(p);
        });
    }
}