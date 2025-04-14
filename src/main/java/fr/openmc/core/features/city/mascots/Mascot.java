package fr.openmc.core.features.city.mascots;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;

@Setter
@Getter
public class Mascot {

    private String cityUuid;
    private String mascotUuid;
    private int level;
    private boolean immunity;
    private long immunity_time;
    private boolean alive;
    private Chunk chunk;

    public Mascot(String cityUuid, String mascotUuid, int level, boolean immunity, long immunity_time, boolean alive, Chunk chunk) {
        this.cityUuid = cityUuid;
        this.mascotUuid = mascotUuid;
        this.level = level;
        this.immunity = immunity;
        this.immunity_time = immunity_time;
        this.alive = alive;
        this.chunk = chunk;
    }
}

