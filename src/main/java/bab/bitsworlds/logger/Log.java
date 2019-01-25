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

    public Log(LogAction action, Object data, LogRecorder recorder, String description, Timestamp time, UUID world) {
        this.action = action;
        this.data = data;
        this.recorder = recorder;
        this.description = description;
        this.time = time;
        this.world = world;
    }

    public boolean isGlobal() {
        return world == null;
    }
}