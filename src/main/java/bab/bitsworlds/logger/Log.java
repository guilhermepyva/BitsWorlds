package bab.bitsworlds.logger;

import java.sql.Timestamp;
import java.util.UUID;

public class Log {
    public LogAction action;
    public Object data;
    public LogRecorder recorder;
    public String description;
    public Timestamp time;

    public UUID world;
    public String worldName;

    public Log(LogAction action, Object data, LogRecorder recorder, String description, Timestamp time, UUID world, String worldName) {
        this.action = action;
        this.data = data;
        this.recorder = recorder;
        this.description = description;
        this.time = time;
        this.world = world;
        this.worldName = worldName;
    }

    public boolean isGlobal() {
        return world == null;
    }
}