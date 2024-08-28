package de.ole101.chained.common.models;

import lombok.Builder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitTask;

@Data
@Builder
public class Chain {

    private final Player player;
    private final Player target;
    private Slime slime;
    private BukkitTask task;

    public void attachLeash() {
        Location offset = this.target.getLocation().add(0, 0.5, 0);
        this.slime = this.target.getWorld().spawn(offset, Slime.class, slime -> {
            slime.setSize(0);
            slime.setAI(false);
            slime.setSilent(true);
            slime.setGravity(false);
            slime.setInvisible(true);
            slime.setNoPhysics(true);
            slime.setCollidable(false);
            slime.setInvulnerable(true);
            slime.setLeashHolder(this.player);
        });
    }
}
